package cc.kercheval.bccmusic.ws_bccmusic_api.security.evaluator;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.CollaborationType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaborationAccount;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.CollaboratorRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("collaboratorPermissionEvaluator")
public class CollaboratorPermissionEvaluator {

    private final CollaboratorRepository collaboratorRepository;
    private final AccountService accountService;

    public boolean hasFullEditScoresPermission(Long accountId, Authentication auth) {
    	List<String> validPermissions = List.of(CollaborationType.SCORE_EDIT.name(), CollaborationType.SCORE_COLLAB_EDIT.name());
    	
        if (auth == null || !auth.isAuthenticated()) return false;

        String username = auth.getName();
        Account currentUser = accountService.findByUsername(username);
        log.info("Checking Permissions for user: {} with accountId: {}", currentUser.getAccountName(), currentUser.getAccountId());
        Collaborator collaborator = collaboratorRepository.findByOwnerAccountIdAndCollaboratorAccountId(accountId, currentUser.getAccountId()).orElse(null);

        boolean isOwner = accountId.equals(currentUser.getAccountId());
        
        boolean hasFullScoreEdit = collaborator != null && validPermissions.contains(collaborator.getPermissionLevel());

        return isOwner || hasFullScoreEdit;
    }
    
    public boolean hasBasicEditScoresPermission(Long ownerAccountId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        log.info("Verifying user has edit permission for account: {}.", ownerAccountId);
        String username = auth.getName();
        log.info("Checking permission for username: {}", username);

        Account currentUser = accountService.findByUsername(username);
        if (currentUser == null) {
            log.warn("No account found for username: {}", username);
            return false;
        }

        log.info("Found user: id={}, type={}", currentUser.getAccountId(), currentUser.getAccountType());

        if (currentUser.getAccountId().equals(ownerAccountId)) {
            log.info("User is owner of account {}", ownerAccountId);
            return true;
        }

        List<String> validPermissions = List.of(
            CollaborationType.LIMITED_SCORE_EDIT.name(),
            CollaborationType.SCORE_EDIT.name(),
            CollaborationType.SCORE_COLLAB_EDIT.name()
        );

        List<CollaborationAccount> allowed = collaboratorRepository.findAllowedOwners(currentUser.getAccountId());

        boolean hasPermission = allowed.stream()
            .anyMatch(ca -> 
                ca.getOwnerAccountId().equals(ownerAccountId) && 
                validPermissions.contains(ca.getPermissionLevel())
            );

        log.info("Permission check result for owner {}: {}", ownerAccountId, hasPermission);

        return hasPermission;
    }
    
    public boolean hasEditCollaboratorPermission(Long collaboratorId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;

        String username = auth.getName();
        Account currentUser = accountService.findByUsername(username);

        Collaborator collab = collaboratorRepository.findById(collaboratorId)
            .orElseThrow(() -> new EntityNotFoundException("Not found"));

        boolean isOwner = collab.getOwner().getAccountId().equals(currentUser.getAccountId());
        boolean hasCollabEdit = collaboratorRepository.existsByCollaboratorAccountIdAndOwnerAccountIdAndPermissionLevel(
            currentUser.getAccountId(),
            collab.getOwner().getAccountId(),
            CollaborationType.SCORE_COLLAB_EDIT.name()
        );

        return isOwner || (hasCollabEdit && !currentUser.getAccountId().equals(collab.getCollaborator().getAccountId()));
    }
    
    public boolean hasViewScoresPermission(Long accountId, Authentication auth) {
    	if (auth == null || !auth.isAuthenticated()) return false;
    	
        String username = auth.getName();
        Account currentUser = accountService.findByUsername(username);
        
        List<String> validPermissions = List.of(CollaborationType.VIEW_ONLY.name(), CollaborationType.LIMITED_SCORE_EDIT.name(), CollaborationType.SCORE_EDIT.name(), CollaborationType.SCORE_COLLAB_EDIT.name());
        Collaborator collaborator = collaboratorRepository.findByOwnerAccountIdAndCollaboratorAccountId(accountId, currentUser.getAccountId()).orElse(null);
        
        boolean isOwner = collaborator.getOwner().getAccountId().equals(currentUser.getAccountId());
        boolean hasViewPermission = collaborator != null && validPermissions.contains(collaborator.getPermissionLevel());

        return isOwner || hasViewPermission;
    }
}
