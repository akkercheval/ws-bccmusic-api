package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
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
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaborationAccount;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.Collaborator;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.CollaboratorService;
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
	
	@GetMapping
	public List<Collaborator> getMyCollaborators(Principal principal) {
		Long myAccountId = getAccountIdFromPrincipal(principal).getAccountId();
		return collaboratorService.getMyCollaborators(myAccountId).stream()
				.map(collaborator -> modelMapper.map(collaborator, Collaborator.class))
				.toList();
	}
	
	@GetMapping("/my-collaborations")
	public List<CollaborationAccount> getMyCollaborationAccounts(Principal principal) {
		log.info("Fetching valid collaborations.");
		List<CollaborationAccount> collaborations = collaboratorService.getMyCollaborationAccounts(getAccountIdFromPrincipal(principal));
		log.info("Collaborations: {}", collaborations.toString());
		return collaborations;
	}
	
	@PostMapping
	@PreAuthorize("@collaboratorPermissionEvaluator.hasEditCollaboratorPermission(#collaboratorId, authentication)")
	public Collaborator addCollaborator(@Valid @RequestBody Collaborator collaborator) throws AccountValidationException, AccountNotFoundException {
	    return modelMapper.map(collaboratorService.addNewCollaborator(
				modelMapper.map(collaborator, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator.class)), Collaborator.class);
	}
	
	@PutMapping("/{collaboratorId}")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasEditCollaboratorPermission(#collaboratorId, authentication)")
	public Collaborator updateCollaborator(@Valid @RequestBody Collaborator collaborator) {
	    return modelMapper.map(collaboratorService.updateCollaborator(
	    		modelMapper.map(collaborator, cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator.class)), Collaborator.class);
	}
	
	@DeleteMapping("/{collaboratorId}")
	@PreAuthorize("@collaboratorPermissionEvaluator.hasEditCollaboratorPermission(#collaboratorId, authentication)")
	public void deleteCollaborator(@PathVariable Long collaboratorId) throws AccountValidationException, AccountNotFoundException {
	    collaboratorService.deleteCollaborator(collaboratorId);
	}
	
	private Account getAccountIdFromPrincipal(Principal principal) {
		return accountService.findByUsername(principal.getName());
	}
}
