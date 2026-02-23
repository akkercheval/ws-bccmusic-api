package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScoreComposer {
	Long scoreComposerId;
	@NotNull
	Long scoreId;
	@NotNull
	Composer composer;
	@NotBlank
	String contributionType;
}
