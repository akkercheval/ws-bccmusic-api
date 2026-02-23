package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountValidationException extends RuntimeException {
	
	private static final long serialVersionUID = 4382480463228763265L;

	private final List<String> errors;

    public AccountValidationException(List<String> errors) {
        super("Account validation failed");
        this.errors = errors != null ? new ArrayList<>(errors) : List.of();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

}
