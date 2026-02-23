package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

public class CollaborationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3926569603841267804L;

	public CollaborationNotFoundException(Long collaboratorId) {
		super(String.format("Could not find Collaboration with id: %d", collaboratorId));
	}

}
