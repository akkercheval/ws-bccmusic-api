package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.CollaborationType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.CollaborationAlreadyExistsException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.CollaborationNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.CollaborationValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaborationInfo;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaboratorRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.CollaboratorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollaboratorService {
	private final ModelMapper modelMapper;
	private final CollaboratorRepository collaboratorRepository;
	private final AccountService accountService;
	private final AccountRepository accountRepository;
	
	@Transactional
	public Collaborator addNewCollaborator(CollaboratorRequest request, String currentUsername) throws AccountValidationException, AccountNotFoundException {
		Long ownerId = request.getOwnerAccountId();
	    Long collaboratorId = request.getCollaboratorAccountId();
	    Account granter;
                
        if(ownerId.equals(collaboratorId)) {
        	throw new IllegalArgumentException("Cannot add self as a collaborator");
        }
		
		if(collaboratorRepository.existsByOwnerAccountIdAndCollaboratorAccountId(ownerId, collaboratorId)) {
			throw new CollaborationAlreadyExistsException(ownerId, collaboratorId);
		}
		
		Account owner = accountRepository.findById(ownerId)
		        .orElseThrow(() -> new AccountNotFoundException("Owner not found"));

	    Account collaborator = accountRepository.findById(collaboratorId)
	        .orElseThrow(() -> new AccountNotFoundException("Collaborator not found"));
	    
	    if(owner.getUsername().equals(currentUsername)) {
	    	granter=owner;
	    } else {
	    	granter=accountRepository.findByUsername(currentUsername);
	    }
		
		Collaborator newCollaborator = new Collaborator();
	    newCollaborator.setOwner(owner);
	    newCollaborator.setCollaborator(collaborator);
	    newCollaborator.setPermissionLevel(request.getPermissionLevel());
	    newCollaborator.setGrantedAt(LocalDateTime.now());
	    newCollaborator.setGrantedBy(granter);
		
		Collaborator savedCollaborator = collaboratorRepository.save(newCollaborator);
		
		log.info("Added collaborator for owner {} by {}", newCollaborator.getOwner().getAccountId(), newCollaborator.getGrantedBy().getAccountId());
		
		if(Role.VIEWER.name().equalsIgnoreCase(savedCollaborator.getCollaborator().getAccountType().name())) {
			Account collaboratorAccount = savedCollaborator.getCollaborator();
			collaboratorAccount.setAccountType(Role.COLLABORATOR.name());
			accountService.updateAccount(collaboratorAccount);
			log.info("Updated user {} to Collaborator Role", collaboratorAccount.getUsername());
		}
		
		return savedCollaborator;
	}
	
	@Transactional
	public Collaborator updateCollaborator(Collaborator updatedCollaborator) {
		Optional<Collaborator> collaboratorOptional = collaboratorRepository.findById(updatedCollaborator.getCollaboratorId());
		if(collaboratorOptional.isEmpty()) {
			throw new CollaborationNotFoundException(updatedCollaborator.getCollaboratorId());
		}
		
		Collaborator collaboratorEntity = collaboratorOptional.get();
		
		if((updatedCollaborator.getCollaborator().getAccountId() != collaboratorEntity.getCollaborator().getAccountId()) 
				|| (updatedCollaborator.getOwner().getAccountId() != collaboratorEntity.getOwner().getAccountId())) {
			throw new CollaborationValidationException("Cannot change owner or collaborator for an existing collaboration.  A new collaboration must be created.");
		}
		
		collaboratorEntity.setPermissionLevel(updatedCollaborator.getPermissionLevel());
		collaboratorEntity.setGrantedBy(modelMapper.map(updatedCollaborator.getGrantedBy(), Account.class));
		collaboratorEntity.setGrantedAt(LocalDateTime.now());
		
		return modelMapper.map(collaboratorRepository.save(collaboratorEntity), Collaborator.class);
	}
	
	@Transactional
	public void deleteCollaborator(Long collaboratorId) throws AccountValidationException, AccountNotFoundException {
		Collaborator collaboratorToDelete = collaboratorRepository.findById(collaboratorId)
				.orElseThrow(() -> new CollaborationNotFoundException(collaboratorId));
		
		collaboratorRepository.deleteById(collaboratorId);
		
		if(!collaboratorRepository.existsByOwnerAccountIdOrCollaboratorAccountId(collaboratorId, collaboratorId)) {
			Account accountToUpdate = collaboratorToDelete.getCollaborator();
			accountToUpdate.setAccountType(Role.VIEWER.name());
			accountService.updateAccount(accountToUpdate);
		}
	}

	public List<Collaborator> getMyCollaborators(Long myAccountId) {		
		return collaboratorRepository.findByOwnerAccountId(myAccountId);
	}
	
	@Transactional(readOnly = true)
	public List<CollaborationInfo> getMyAllowedOwnerAccounts(Account myAccount) {
		log.info("Getting My Allowed Owner Accounts for: {}", myAccount.getAccountName());
	    String myType = myAccount.getAccountType().name();
    	List<CollaborationInfo> collaborationInfos = new ArrayList<CollaborationInfo>();

	    if (myType.equals(Role.ADMINISTRATOR.name())) {
	    	List<Account> allOwners = accountService.findAllOwners();

	    	for(Account account: allOwners) {
	    		collaborationInfos.add(new CollaborationInfo(
	                    account.getAccountId(),
	                    account.getAccountName(),
	                    CollaborationType.SCORE_COLLAB_EDIT.name()
	                ));
	    	}
	    	collaborationInfos.sort(Comparator.comparing(
	    	        CollaborationInfo::getOwnerAccountName,
	    	        String.CASE_INSENSITIVE_ORDER
	    	    ));
	    	log.info("CollaborationInfos: {}", collaborationInfos.toString());
	    	return collaborationInfos;
	    }

	    if (myType.equals(Role.OWNER.name())) {
	    	collaborationInfos.add(new CollaborationInfo(myAccount.getAccountId(), myAccount.getAccountName(), CollaborationType.SCORE_COLLAB_EDIT.name()));
	    }
	    
	    List<CollaborationInfo> myCollabInfos = collaboratorRepository.findMyAllowedOwnerCollaborationInfos(myAccount.getAccountId());

	    myCollabInfos.sort(Comparator.comparing(
	        CollaborationInfo::getOwnerAccountName,
	        String.CASE_INSENSITIVE_ORDER
	    ));

	    collaborationInfos.addAll(myCollabInfos);
	    log.info("CollaborationInfos: {}", collaborationInfos.toString());
	    return collaborationInfos;
	}
	public List<Account> getAvailableCollaborators(Account account) {
		return accountRepository.findAvailableCollaborators(account.getAccountId());
	}

	public Collaborator getCollaborator(Long collaboratorId) {
		return collaboratorRepository.findById(collaboratorId).get();
	}
}
