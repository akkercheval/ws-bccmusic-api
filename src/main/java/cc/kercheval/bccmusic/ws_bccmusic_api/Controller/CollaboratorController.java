package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountNotFoundException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.AccountValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.CollaborationValidationException;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.AccountInfo;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.Collaborator;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaboratorRequest;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.CollaboratorService;
import cc.kercheval.bccmusic.ws_bccmusic_api.security.evaluator.CollaboratorPermissionEvaluator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {
	
	private final CollaboratorService collaboratorService;
	private final ModelMapper modelMapper;
	private final AccountService accountService;
	private final CollaboratorPermissionEvaluator collaboratorPermissionEvaluator;
	
	@GetMapping
	public List<Collaborator> getMyCollaborators(Principal principal) throws AccountNotFoundException {
		if(principal == null || principal.getName() == null) {
			log.warn("Cannot getMyCollaborators.  Principal is null.");
			throw new CollaborationValidationException("Cannot get My Collaborators. User is not logged in or username is null.");
		}
			 
		log.info("Principal: {}", principal.getName());
		Account myAccount = getAccountIdFromPrincipal(principal);
		if(myAccount == null) {
			log.warn("Cannot getMyCollaborators.  User Account does not exist or was not found.");
			throw new AccountNotFoundException("Cannot get Collaborators: Account Not found.");
		}
		log.info("My AccountId: {}", myAccount.getAccountId());
		return collaboratorService.getMyCollaborators(myAccount.getAccountId()).stream()
				.map(collaborator -> modelMapper.map(collaborator, Collaborator.class))
				.toList();
	}
	
	@GetMapping("/{collaboratorId}")
	public Collaborator getCollaborator(Long collaboratorId) {
		log.info("Collaborator Id to find: {}", collaboratorId);
		if(collaboratorId == null)
			return null;
		return modelMapper.map(collaboratorService.getCollaborator(collaboratorId), Collaborator.class);
	}
	
	
	@GetMapping("/available-collaborators")
	public List<AccountInfo> getAvailableCollaborators(Principal principal) {
		List<Account> availableAccounts = collaboratorService.getAvailableCollaborators(getAccountIdFromPrincipal(principal));
		return availableAccounts.stream()
				.map(account ->	modelMapper.map(account, AccountInfo.class))
				.toList();
	}
	
	@PostMapping
	@PreAuthorize("@collaboratorPermissionEvaluator.hasEditCollaboratorPermission(#request.ownerAccountId, authentication)")
	public Collaborator addCollaborator(@Valid @RequestBody CollaboratorRequest request, Principal principal) throws AccountValidationException, AccountNotFoundException {
	    return modelMapper.map(
	        collaboratorService.addNewCollaborator(request, principal.getName()), 
	        Collaborator.class
	    );
	}
	
	@PutMapping("/{collaboratorId}")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasEditCollaboratorPermission(#collaborator.owner.accountId, authentication)")
	public Collaborator updateCollaborator(@Valid @RequestBody Collaborator collaborator) {
	    return modelMapper.map(collaboratorService.updateCollaborator(
	    		modelMapper.map(collaborator, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator.class)), Collaborator.class);
	}
	
	@DeleteMapping("/{collaboratorId}")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasEditCollaboratorPermission(#collaboratorId, authentication)")
	public void deleteCollaborator(@PathVariable Long collaboratorId) throws AccountValidationException, AccountNotFoundException {
	    collaboratorService.deleteCollaborator(collaboratorId);
	}
	
	@GetMapping("/{collaboratorId}/can-edit")
	public boolean canEditCollaborator(@PathVariable Long collaboratorId, Authentication authentication) {
	    return collaboratorPermissionEvaluator.hasEditCollaboratorPermission(collaboratorId, authentication);
	}
	
	private Account getAccountIdFromPrincipal(Principal principal) {
		return accountService.findByUsername(principal.getName());
	}
}
