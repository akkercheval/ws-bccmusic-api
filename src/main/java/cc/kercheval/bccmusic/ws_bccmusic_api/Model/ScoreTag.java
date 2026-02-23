package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreTag {
	String scoreTagId;
	@NotNull
	Long scoreId;
	@NotBlank
	String tag;	
}
