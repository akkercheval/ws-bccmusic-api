package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import lombok.Data;

@Data
public class ArrangementType {
	private String code;
	private String name;
	private String description;
	private Integer sortOrder;
}
