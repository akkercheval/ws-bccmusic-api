package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountAlreadyExistsException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;
    
    @Test
    @DisplayName("getAccountById when exists then return Account")
    void getAccountById_whenExists_thenReturnAccount() {
        
        Long expectedId = 123L;

        Account mockAccount = new Account();
        mockAccount.setAccountId(expectedId);
        mockAccount.setAccountName("Test User");
        mockAccount.setUsername("testuser");
        mockAccount.setHashedPassword("$2a$12$hashed");
        mockAccount.setCity("Bree");
        mockAccount.setStateAbbr("IN");
        mockAccount.setZipCode("12345");
        mockAccount.setPhoneNumber("5556667777");
        mockAccount.setAccountType(Role.VIEWER.name());

        when(accountRepository.findById(anyLong()))
            .thenReturn(Optional.of(mockAccount));

        Account result = accountService.getAccountById(expectedId);

        assertNotNull(result);
        assertEquals(expectedId, result.getAccountId());
        assertEquals("Test User", result.getAccountName());
        assertEquals(Role.VIEWER, result.getAccountType());
    }
    
    @Test
    @DisplayName("getAccountById when no account exists then throw exception")
    void getAccountById_whenNotFound_thenThrow() throws EntityNotFoundException {
    	
    	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.empty());
    	
        assertThatThrownBy(() -> accountService.getAccountById(1L))
    		.isInstanceOf(EntityNotFoundException.class)
    		.hasMessage("Account not found with id: 1")
    	;
    }

    @Test
    @DisplayName("createAccount should validate information and save account")
    void createAccount_whenValidInput_thenHashPassAndSave() throws AccountValidationException {

        Account account = new Account();
        account.setAccountName("Test User");
        account.setUsername("testuser");
        account.setHashedPassword("$2a$12$hashed");
        account.setAccountType("INVALID");
        account.setCity("Bree");
        account.setStateAbbr("IN");
        account.setZipCode("12345");
        
        when(accountRepository.countUsername("testuser")).thenReturn(0);

        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account saved = inv.getArgument(0);
            saved.setAccountId(1001L);  
            saved.setAccountName("Test User");
            saved.setUsername("testuser");
            saved.setHashedPassword("$2a$12$hashed");
            saved.setCity("Bree");
            saved.setStateAbbr("IN");
            saved.setZipCode("12345");
            saved.setPhoneNumber("5556667777");
            saved.setAccountType(Role.VIEWER.name());

            return saved;
        });

        Account result = accountService.createAccount(account);

        assertEquals(1001L, result.getAccountId());
        assertEquals("$2a$12$hashed", result.getHashedPassword());
        assertEquals(Role.VIEWER, result.getAccountType());

        verify(accountRepository).save(argThat(acc -> 
            acc.getUsername().equals("testuser") &&
            acc.getHashedPassword().equals("$2a$12$hashed")
        ));
    }

    @Test   
    @DisplayName("createAccount should throw execption when no username")
    void createAccount_whenNoUsername_thenThrowValidationException() throws AccountValidationException {

        Account account = new Account();
        account.setAccountName("Test User");
        account.setHashedPassword("$2a$12$hashed");
        account.setAccountType("INVALID");

        assertThatThrownBy(() -> accountService.createAccount(account))
        	.isInstanceOf(AccountValidationException.class)
        	.hasMessage("Account validation failed");
    }
    
    @Test   
    @DisplayName("createAccount should throw execption when multiple validation errors")
    void createAccount_whenMultipleErrors_thenThrowValidationException() throws AccountValidationException {

        Account account = new Account();
        account.setAccountName("Test User");
        account.setUsername("ialreadyexist");
        account.setHashedPassword("$2a$12$hashed");
        account.setStreetAddress("123 Testing Street");
        account.setCity("Bree");
        account.setStateAbbr("ER");
        account.setZipCode("INVALID");
        account.setEmail("abcdef");
        account.setPhoneNumber("ABC-4444");

        when(accountRepository.countUsername("ialreadyexist")).thenReturn(1);
        
        assertThatThrownBy(() -> accountService.createAccount(account))
        	.isInstanceOf(AccountValidationException.class)
        	.hasMessage("Account validation failed");
    }
    
    @Test
    @DisplayName("updateAccount should save when valid data")
    void updateAccount_whenValidData_thenSave() throws AccountValidationException, AccountNotFoundException {
        Account accountWithUpdates = new Account();
        accountWithUpdates.setAccountId(123L);
        accountWithUpdates.setAccountName("Test User");
        accountWithUpdates.setUsername("ialreadyexist");
        accountWithUpdates.setHashedPassword("$2a$12$hashed");
        accountWithUpdates.setStreetAddress("123 Testing Street");
        accountWithUpdates.setCity("Bree");
        accountWithUpdates.setAccountType(Role.VIEWER.name());
        
        Account existingAccount = new Account();
        existingAccount.setAccountName("Test User");
        existingAccount.setUsername("ialreadyexist");
        existingAccount.setHashedPassword("$2a$12$hashed");
        
        when(accountRepository.getAccountByAccountId(123L)).thenReturn(existingAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(accountWithUpdates);
        
        Account result = accountService.updateAccount(accountWithUpdates);
        
        assertEquals(123L, result.getAccountId());
        assertEquals("Bree", result.getCity());
    }
    
    @Test
    @DisplayName("updateAccount should throw when account not found")
    void updateAccount_whenAccountNotFound_thenThrow() throws AccountValidationException, AccountNotFoundException {
        Account accountWithUpdates = new Account();
        accountWithUpdates.setAccountId(123L);
        accountWithUpdates.setAccountName("Test User");
        accountWithUpdates.setUsername("ialreadyexist");
        accountWithUpdates.setHashedPassword("$2a$12$hashed");
        accountWithUpdates.setStreetAddress("123 Testing Street");
        accountWithUpdates.setCity("Bree");
        accountWithUpdates.setAccountType(Role.VIEWER.name());
        
        when(accountRepository.getAccountByAccountId(123L)).thenReturn(null);
        
        assertThatThrownBy(() -> accountService.updateAccount(accountWithUpdates))
    		.isInstanceOf(AccountNotFoundException.class)
    		.hasMessage("Cannot update account: 123.  Account does not exist.");        
    }
    
    @Test
    @DisplayName("updateAccount should throw when updated Username exists")
    void updateAccount_whenUpdatedUsernameExists_thenThrow() throws AccountValidationException, AccountNotFoundException {
        Account accountWithUpdates = new Account();
        accountWithUpdates.setAccountId(123L);
        accountWithUpdates.setAccountName("Test User");
        accountWithUpdates.setUsername("ialsoexist");
        accountWithUpdates.setHashedPassword("$2a$12$hashed");
        accountWithUpdates.setStreetAddress("456 Testing Street");
        accountWithUpdates.setCity("Bree");
        accountWithUpdates.setAccountType(Role.OWNER.name());
        
        Account existingAccount = new Account();
        existingAccount.setAccountId(123L);
        existingAccount.setAccountName("Test User");
        existingAccount.setUsername("ialreadyexist");
        existingAccount.setHashedPassword("$2a$12$hashed");
        existingAccount.setStreetAddress("123 Testing Street");
        existingAccount.setCity("Bree");
        existingAccount.setAccountType(Role.VIEWER.name());
        
        when(accountRepository.getAccountByAccountId(123L)).thenReturn(existingAccount);
        when(accountRepository.countUsername("ialsoexist")).thenReturn(1);
        
        assertThatThrownBy(() -> accountService.updateAccount(accountWithUpdates))
    		.isInstanceOf(AccountAlreadyExistsException.class)
    		.hasMessage("Cannot update username.  Username already exists.");        
    }
}
