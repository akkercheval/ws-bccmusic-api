package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name="Medley")
@Data
public class Medley {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="medley_id", nullable=false, unique=true)
	Long medleyId;
	
	@ManyToOne
	@JoinColumn(name="score_id", foreignKey = @ForeignKey(name="FK_medley_score"))
	Score score;
	
	@Column(name="piece_title", length=100, nullable=false, unique=false)
	String pieceTitle;
	
	@ManyToOne
	@JoinColumn(name="composer_id")
	Composer composer;
}
