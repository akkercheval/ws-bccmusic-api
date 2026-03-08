package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Composer {
	private Long composerId;
	private String firstName;
	private String middleName;
	@NotBlank
	private String lastName;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String fullName;
	
	@Override
    public String toString() {
        return "Composer{" +
               "composerId=" + composerId +
               ", firstName='" + firstName + '\'' +
               ", middleName='" + middleName + '\'' +
               ", lastName='" + lastName + '\'' +
               '}';
    }
}
