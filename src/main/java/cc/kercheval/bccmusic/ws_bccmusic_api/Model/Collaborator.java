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
public class Collaborator {

    private Long collaboratorId;
    private Account owner;
    private Account collaborator;
    private LocalDateTime grantedAt;
    private Account grantedBy;
    private String permissionLevel;
}