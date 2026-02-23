package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import jakarta.validation.constraints.NotBlank;

public class Composer {
	String composerId;
	String firstName;
	String middleName;
	@NotBlank
	String lastName;
	String fullName;
}
