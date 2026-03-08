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
public class Part {
	private Long partId;
	@NotNull
	private Long scoreId;
	@NotBlank
	private String instrument;
	@NotNull
	private boolean hasSolo;
	@NotNull
	private int regularPartCount;
	private int flexMinPart;
	private int flexPartCount;
	private String partComments;
}
