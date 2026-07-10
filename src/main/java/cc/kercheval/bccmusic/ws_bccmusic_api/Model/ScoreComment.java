package cc.kercheval.bccmusic.ws_bccmusic_api.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScoreComment {
    private Long commentId;
    @NotNull
    private Long scoreId;
    @NotBlank
    private String comment;
    private Long createdByAccountId;
    private String createdByAccountName;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private Long updatedByAccountId;
    private String updatedByAccountName;
    private String updatedByUsername;
    private LocalDateTime updatedAt;
}
