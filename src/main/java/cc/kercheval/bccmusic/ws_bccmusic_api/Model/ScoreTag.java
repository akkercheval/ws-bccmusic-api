package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreTag {
	String scoreTagId;
	@NotNull
	Long scoreId;
	@NotBlank
	String tag;	
}
