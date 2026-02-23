package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="Vendor")
public class Vendor extends AuditEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="vendor_id")
	Long vendorId;
	
	@Column(name="vendor_name", length=50, nullable=false)
	String vendorName;
	
	@Column(name="street_address", length=50, nullable=true, unique=false)
	String streetAddress;
	
	@Column(name="city", length=50, nullable=true, unique=false)
	String city;
	
	@Column(name="state_abbr", columnDefinition = "char(2)", length=2, nullable=true, unique=false)
	String stateAbbr;
	
	@Column(name="zip_code", length=10, nullable=true, unique=false)
	String zipCode;
	
	@Column(name="phone_number", length=20, nullable=true, unique=false)
	String phoneNumber;
	
	@Column(name="phone_type", length=15, nullable=true, unique=false)
	String phoneType;
	
	@Column(name="website", length=100, nullable=true, unique=false)
	String website;
	
	@Column(name="email", length=100, nullable=true, unique=false)
	String email;
	
	@Column(name="comments", length=200, nullable=true, unique=false)
	String comments;
}
