package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollaboratorRequest {
	private Long collaboratorId;
    private Long ownerAccountId;
    private Long collaboratorAccountId;
    private String permissionLevel;
}