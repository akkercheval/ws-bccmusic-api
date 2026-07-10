package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountUpgradeRequest {
    private Long requestId;
    private Long accountId;
    private String accountName;
    private String status;
    private String reason;
    private String adminNotes;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
    private String resolvedByUsername;
}
