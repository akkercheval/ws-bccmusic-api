package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.CollaborationType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.CollaborationAlreadyExistsException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaboratorRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.CollaboratorRepository;

@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {
	@Mock
	private ModelMapper modelMapper;
	
	@Mock
	private CollaboratorRepository collaboratorRepository;
	
	@Mock
	private AccountService accountService;
	
	@Mock
	private AccountRepository accountRepository;
	
	@InjectMocks
	private CollaboratorService collaboratorService;
	
	@Test
	@DisplayName("addNewCollaborator when successful save")
	void addNewCollaborator_whenValidAccount_thenSave() throws AccountValidationException, AccountNotFoundException {
		Account ownerAccount = createOwnerAccount();
		Account viewerAccount = createViewerAccount();
				
		CollaboratorRequest testCollaborator = new CollaboratorRequest();
		testCollaborator.setCollaboratorAccountId(viewerAccount.getAccountId());
		testCollaborator.setOwnerAccountId(ownerAccount.getAccountId());
		testCollaborator.setPermissionLevel(CollaborationType.SCORE_EDIT.name());
		
		when(accountRepository.findById(ownerAccount.getAccountId())).thenReturn(Optional.of(ownerAccount));
		when(accountRepository.findById(viewerAccount.getAccountId())).thenReturn(Optional.of(viewerAccount));
		
		when(collaboratorRepository.existsByOwnerAccountIdAndCollaboratorAccountId(anyLong(), anyLong())).thenReturn(false);
		
		ArgumentCaptor<Collaborator> collaboratorCaptor = ArgumentCaptor.forClass(Collaborator.class);
	    when(collaboratorRepository.save(collaboratorCaptor.capture()))
	        .thenAnswer(invocation -> {
	            Collaborator saved = invocation.getArgument(0);
	            saved.setCollaboratorId(100L); 
	            return saved;
	        });
	    
	    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountService.updateAccount(accountCaptor.capture()))
            .thenAnswer(invocation -> invocation.getArgument(0));
	    
	    Collaborator result = collaboratorService.addNewCollaborator(testCollaborator, "owneraccount");
	    Collaborator capturedCollaborator = collaboratorCaptor.getValue();
	    
	    assertTrue(capturedCollaborator.getGrantedAt().isBefore(LocalDateTime.now().plusSeconds(1)));

	    assertEquals(123L, capturedCollaborator.getOwner().getAccountId());
	    assertEquals(456L, capturedCollaborator.getCollaborator().getAccountId());
	    assertEquals(123L, capturedCollaborator.getGrantedBy().getAccountId());
	    assertEquals(CollaborationType.SCORE_EDIT.name(), capturedCollaborator.getPermissionLevel());

	    
        verify(accountService, times(1)).updateAccount(any(Account.class));
	    Account updatedAccount = accountCaptor.getValue();
        assertNotNull(updatedAccount, "updateAccount should have been called");
        assertEquals(456L, updatedAccount.getAccountId());
        assertEquals(Role.COLLABORATOR.name(), updatedAccount.getAccountType().name(),
            "Viewer should have been upgraded to COLLABORATOR");

	    assertEquals(100L, result.getCollaboratorId());
	}
	
	@Test
	@DisplayName("addNewCollaborator when non-viewer")
    void addNewCollaborator_whenNonViewer_doNotCallUpdateAccount() throws AccountValidationException, AccountNotFoundException {

        Account owner = createOwnerAccount();
        Account collaborator = createCollaboratorAccount();

        CollaboratorRequest testCollaborator = new CollaboratorRequest();
		testCollaborator.setCollaboratorAccountId(collaborator.getAccountId());
		testCollaborator.setOwnerAccountId(owner.getAccountId());
        testCollaborator.setPermissionLevel(CollaborationType.SCORE_EDIT.name());

        when(collaboratorRepository.existsByOwnerAccountIdAndCollaboratorAccountId(anyLong(), anyLong()))
            .thenReturn(false);
		when(accountRepository.findById(owner.getAccountId())).thenReturn(Optional.of(owner));
		when(accountRepository.findById(collaborator.getAccountId())).thenReturn(Optional.of(collaborator));

        ArgumentCaptor<Collaborator> captor = ArgumentCaptor.forClass(Collaborator.class);
        when(collaboratorRepository.save(captor.capture()))
            .thenAnswer(inv -> {
                Collaborator c = inv.getArgument(0);
                c.setCollaboratorId(999L);
                return c;
            });

        collaboratorService.addNewCollaborator(testCollaborator, "owneraccount");

        verify(accountService, never()).updateAccount(any(Account.class));

        Collaborator saved = captor.getValue();
        assertNotNull(saved.getGrantedAt());
    }
	
	@Test
	@DisplayName("addNewCollaborator when owner is collaborator")
	void addNewCollaborator_whenOwnerIsCollaborator_thenException() {
		Account account = createOwnerAccount();
		
		CollaboratorRequest collaborator = new CollaboratorRequest();
		collaborator.setCollaboratorAccountId(account.getAccountId());
		collaborator.setOwnerAccountId(account.getAccountId());
		collaborator.setPermissionLevel(CollaborationType.SCORE_COLLAB_EDIT.name());
		
		assertThatThrownBy(() -> collaboratorService.addNewCollaborator(collaborator, "owneraccount"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Cannot add self as a collaborator");
	}
	
	@Test
	@DisplayName("addNewCollaborator when collaboration exists")
	void addNewCollaborator_whenCollaborationExists() {
		Account ownerAccount = createOwnerAccount();
		Account collaboratorAccount = createViewerAccount();
		
		CollaboratorRequest collaborator = new CollaboratorRequest();
		collaborator.setCollaboratorAccountId(collaboratorAccount.getAccountId());
		collaborator.setOwnerAccountId(ownerAccount.getAccountId());
		collaborator.setPermissionLevel(CollaborationType.SCORE_EDIT.name());
		
		when(collaboratorRepository.existsByOwnerAccountIdAndCollaboratorAccountId(anyLong(), anyLong())).thenReturn(true);
		
		assertThatThrownBy(() -> collaboratorService.addNewCollaborator(collaborator, "collabaccount"))
		.isInstanceOf(CollaborationAlreadyExistsException.class);
	}
	
	private Account createOwnerAccount() {
		return Account.builder()
				.accountId(123L)		
				.accountName("Owner Account")
				.accountType(Role.OWNER)
				.username("owneraccount")
				.build();
	}
	
	private Account createViewerAccount() {
		return Account.builder()
			.accountId(456L)
			.accountName("Viewer Account")
			.accountType(Role.VIEWER)
			.username("vieweraccount")
		.build();

	}
	
	private Account createCollaboratorAccount() {
		return Account.builder()
				.accountId(789L)
				.accountName("Collaborator Account")
				.accountType(Role.COLLABORATOR)
				.username("collabaccount")
				.build();
	}
}
