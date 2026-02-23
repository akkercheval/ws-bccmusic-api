package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Collaborator {

    private Long collaboratorId;
    private Account owner;
    private Account collaborator;
    private LocalDateTime grantedAt;
    private Account grantedBy;
    private String permissionLevel;
}