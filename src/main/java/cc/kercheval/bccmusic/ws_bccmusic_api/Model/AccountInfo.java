package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
	private Long accountId;
	private String accountName;
	private String username;
}
