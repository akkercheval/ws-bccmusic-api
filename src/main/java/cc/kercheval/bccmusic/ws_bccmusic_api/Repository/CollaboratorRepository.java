package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaborationAccount;

public interface CollaboratorRepository extends CrudRepository<Collaborator, Long> {
	
	public Optional<Collaborator> findByOwnerAccountIdAndCollaboratorAccountId(Long ownerAccountId, Long collaboratorAccountId);
	
	public List<Collaborator> findByOwnerAccountId(Long ownerAccountId);
	
	public boolean existsByOwnerAccountIdOrCollaboratorAccountId(Long ownerAccountId, Long collaboratorAccountId);
	
	@Query("SELECT new cc.kercheval.bccmusic.ws_bccmusic_api.Model.CollaborationAccount("
			+ "c.owner.accountId, c.owner.accountName, c.collaborator.accountId, c.permissionLevel) "
			+ "FROM Collaborator c "
			+ "WHERE c.collaborator.accountId = :collaboratorAccountId")
	public List<CollaborationAccount> findAllowedOwners(Long collaboratorAccountId);
	
	public boolean existsByOwnerAccountIdAndCollaboratorAccountId(Long ownerAccountId, Long collaboratorAccountId);
		
	public boolean existsByCollaboratorAccountIdAndOwnerAccountIdAndPermissionLevel(Long collaboratorAccountId, Long ownerAccountId, String permissionLevel);
}
