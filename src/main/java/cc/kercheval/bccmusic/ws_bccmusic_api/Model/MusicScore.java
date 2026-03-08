package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MusicScore {
	private Long scoreId;
	@NotBlank
	private String scoreTitle;
	private String scoreSubtitle;
	private Long ownerAccountId;
	private String purchasedName;
	private Date purchasedDate;
	private BigDecimal purchasedCost;
	private BigDecimal grade;
	@NotBlank
	private String arrangementTypeCode;
	private List<ScoreComposer> composers;
	private List<Part> parts;
	private List<ScoreTag> tags;
	private List<Medley> medleyPieces;	
}
