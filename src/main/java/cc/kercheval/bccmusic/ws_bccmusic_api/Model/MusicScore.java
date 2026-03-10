package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
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
	private Account owner;
	private Vendor purchasedFrom;
	private Date purchasedDate;
	private BigDecimal purchasedCost;
	private BigDecimal grade;
	private LocalDateTime createdAt;
	private Account createdBy;
	private LocalDateTime updatedAt;
	private Account updatedBy;
	@NotBlank
	private ArrangementType arrangementType;
	private List<ScoreComposer> scoreComposers;
	private List<Part> parts;
	private List<ScoreTag> scoreTags;
	private List<Medley> medleys;	
}
