package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

public class AccountAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 7745467822899804712L;

	public AccountAlreadyExistsException(String errorMessage) {
		super(errorMessage);
	}
}
