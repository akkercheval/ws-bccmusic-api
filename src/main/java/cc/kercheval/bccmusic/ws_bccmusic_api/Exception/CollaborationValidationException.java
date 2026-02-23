package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

public class CollaborationValidationException extends RuntimeException {

	private static final long serialVersionUID = 6438596693945021355L;

	public CollaborationValidationException(String errorMessage) {
		super(errorMessage);
	}

}
