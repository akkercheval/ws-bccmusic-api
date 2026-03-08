package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor               
@AllArgsConstructor              
@Builder                         
@ToString(exclude = {"hashedPassword", "deletedBy"})
@Entity(name = "Account")
@Table(name = "Account")
@SQLRestriction(value = "deleted_at IS NULL")
public class Account extends AuditEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="account_id", nullable=false, unique=true)
	Long accountId;
	
	@Column(name="account_name", length=50, nullable=false, unique=true)
	String accountName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", length = 20, nullable = false)
	@Builder.Default
	private Role accountType = Role.VIEWER;
	
	@Column(name="username", length=20, nullable=true, unique = true)
	private String username;
	
	@Column(name="password_hash", nullable=true, length=256)
	private String hashedPassword;
	
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
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by")
	private Account deletedBy;
	
	public void setAccountType(String role) {
		if(role == null || role.isBlank()) {
			this.accountType = Role.VIEWER;
		} else {
			try {
				this.accountType = Role.valueOf(role.toUpperCase());
			} catch (IllegalArgumentException e) {
				this.accountType = Role.VIEWER;
			}
		}
	}
	
	public boolean isActive() {
        return deletedAt == null;
    }
}
