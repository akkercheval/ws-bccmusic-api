package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Vendor {
	private Long vendorId;
	@NotBlank
	private String vendorName;
	private String streetAddress;
	private String city;
	private String stateAbbr;
	private String zipCode;
	private String phoneNumber;
	private String phoneType;
	@URL
	private String website;
	@Email
	private String email;
	private LocalDateTime createdAt;
	private Account createdBy;
	private LocalDateTime updatedAt;
	private Account updatedBy;
}
