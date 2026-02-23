package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Medley {
	String medleyId;
	@NotNull
	Long scoreId;
	@NotBlank
	String pieceTitle;
	@NotNull
	Composer composer;
}
