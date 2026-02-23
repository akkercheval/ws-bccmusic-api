package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
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
