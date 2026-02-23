package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import lombok.Data;

@Data
public class CollaborationAccount {
	private Long ownerAccountId;
	private String ownerAccountName;
	private Long collaboratorAccountId;
	private String permissionLevel;
	
	public CollaborationAccount(Long ownerAccountId, String ownerAccountName,
            Long collaboratorAccountId, String permissionLevel) {
		this.ownerAccountId = ownerAccountId;
		this.ownerAccountName = ownerAccountName;
		this.collaboratorAccountId = collaboratorAccountId;
		this.permissionLevel = permissionLevel;
	}
}
