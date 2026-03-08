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
public class ScoreComposer {
	private Long scoreComposerId;
	@NotNull
	private Long scoreId;
	@NotNull
	private Composer composer;
	@NotBlank
	private String contributionType;
	
	@Override
	public String toString() {
		return "ScoreComposer{" +
				", scoreComposerId=" + scoreComposerId +
				", scoreId=" + scoreId +
				composer.toString() +
				", ContributionType=" + contributionType +
				'}';
	}
}
