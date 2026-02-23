package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Part {
	Long partId;
	@NotNull
	Long scoreId;
	@NotBlank
	String instrument;
	@NotNull
	int partNumber;
	int flexNumber;
	String partComments;
}
