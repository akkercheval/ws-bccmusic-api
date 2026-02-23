package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

public class VendorNotFoundException extends Exception {

	private static final long serialVersionUID = 3434852897865404031L;

	public VendorNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
