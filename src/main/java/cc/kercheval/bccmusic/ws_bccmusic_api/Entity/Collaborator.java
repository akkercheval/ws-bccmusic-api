package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import java.time.LocalDateTime;

import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.CollaborationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Collaborator")
@Data
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collaborator_id")
    private Long collaboratorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborator_account_id", nullable = false)
    private Account collaborator;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private Account grantedBy;

    @Column(name = "permission_level", length = 20, nullable = false)
    private String permissionLevel = CollaborationType.LIMITED_SCORE_EDIT.name();
}