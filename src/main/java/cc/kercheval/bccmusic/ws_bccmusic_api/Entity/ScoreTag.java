package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name="ScoreTag")
@Table(name = "ScoreTag")
@Data
public class ScoreTag {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="score_tag_id", nullable=false, unique=true)
	Long scoreTagId;
	
	@ManyToOne
	@JoinColumn(name="score_id", nullable=false, unique=false)
	Score score;
	
	@Column(name="tag", length=50, nullable=false, unique=false)
	String tag;	
}
