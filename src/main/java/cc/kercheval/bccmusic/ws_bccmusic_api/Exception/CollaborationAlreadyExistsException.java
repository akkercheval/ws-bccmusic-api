package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

public class CollaborationAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 7745467822899804712L;

	public CollaborationAlreadyExistsException(Long ownerId, Long collaboratorId) {
        super(String.format("Collaboration already exists between owner %d and collaborator %d", 
                            ownerId, collaboratorId));
    }
}
