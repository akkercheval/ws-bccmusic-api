package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationInfo {
	private Long ownerAccountId;
	private String ownerAccountName;
    private String permissionLevel;
}
