package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
	private Long accountId;
	@NotBlank
	private String accountName;
	private String accountType;
	private String username;
	private String password;
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
