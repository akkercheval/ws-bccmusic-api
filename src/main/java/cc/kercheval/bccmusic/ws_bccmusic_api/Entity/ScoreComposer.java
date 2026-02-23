package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity(name="ScoreComposer")
@Data
public class ScoreComposer {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="score_composer_id", nullable=false, unique=true)
	Long scoreComposerId;
	
	@ManyToOne
	@JoinColumn(name="score_id", nullable=false, unique=false)
	Score score;
	
	@ManyToOne
	@JoinColumn(name="composer_id", nullable=false, unique=false)
	Composer composer;
	
	@Column(name="contribution_type", length=50, nullable=false, unique=false)
	String contributionType;
}
