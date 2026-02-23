package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

public class ScoreValidationException extends RuntimeException {

	private static final long serialVersionUID = -3820533545531043550L;

	public ScoreValidationException(String errorMessage) {
		super(errorMessage);
	}

}
