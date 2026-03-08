package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="Score")
public class Score extends AuditEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="score_id", nullable=false, unique=true)
	private Long scoreId;
	
	@Column(name="score_title", length=50, nullable=false, unique=false)
	private String scoreTitle;
	
	@Column(name="score_subtitle", length=50, nullable=false, unique=false)
	private String scoreSubtitle;
	
	@ManyToOne
	@JoinColumn(name="account_id", nullable=true)
	private Account owner;
	
	@ManyToOne
	@JoinColumn(name="vendor_id", nullable=true, unique=false)
	private Vendor purchasedFrom;
	
	@Column(name="purchased_date", nullable=true, unique=false)
	private Date purchasedDate;
	
	@Column(name="purchased_cost", nullable=true, precision=10, scale=2)
	private BigDecimal purchasedCost;
	
	@Column(name="grade", nullable=false, unique=false, precision = 3, scale = 1)
	private BigDecimal grade;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "arrangement_type_code", nullable = false)
	private ArrangementType arrangementType;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by")
	private Account deletedBy;
	
	@OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ScoreComposer> scoreComposers = new ArrayList<>();	
	
	@OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("instrument")
	private List<Part> parts = new ArrayList<>();
	
	@OneToMany(mappedBy="score", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ScoreTag> scoreTags;
	
	@OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Medley> medleyPieces = new ArrayList<>();
}
