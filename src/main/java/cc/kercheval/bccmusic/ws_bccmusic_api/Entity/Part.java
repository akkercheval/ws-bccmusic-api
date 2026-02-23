package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity(name="Part")
@Data
public class Part {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="part_id", nullable=false, unique=true)
	Long partId;
	
	@ManyToOne
	@JoinColumn(name="score_id", nullable=false, unique=false)
	Score score;
	
	@Column(name="instrument", length=50, nullable=false, unique=false)
	String instrument;
	
	@Column(name="part_number", nullable=false, unique=false)
	int partNumber;
	
	@Column(name="flex_number", nullable=true, unique=false)
	int flexNumber;
	
	@Column(name="part_comments", length=200, nullable=true, unique=false)
	String partComments;
}
