package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity(name="Composer")
@Data
public class Composer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="composer_id", nullable=false, unique=true)
	Long composerId;
	
	@Column(name="first_name", length=50, nullable=true, unique=false)
	String firstName;
	
	@Column(name="middle_name", length=50, nullable=true, unique=false)
	String middleName;
	
	@Column(name="last_name", length=50, nullable=false, unique=false)
	String lastName;
	
	@Transient
	@Column(name="full_name", length=152, nullable=false)
	String fullName;
}
