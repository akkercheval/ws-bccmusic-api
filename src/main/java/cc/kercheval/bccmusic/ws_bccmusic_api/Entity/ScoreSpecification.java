package cc.kercheval.bccmusic.ws_bccmusic_api.Entity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public final class ScoreSpecification {

    private ScoreSpecification() {}

    public static Specification<Score> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("scoreTitle")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Score> hasAnyTag(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Score, ScoreTag> tagJoin = root.join("scoreTags");
            return tagJoin.get("tag").in(tags);
        };
    }
    
    public static Specification<Score> hasOwner(Long accountId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("accountId"), accountId);
    }

    public static Specification<Score> hasComposer(Long composerId) {
        return (root, query, cb) -> 
            composerId == null ? null :
            cb.equal(root.join("scoreComposers").get("composer").get("composerId"), composerId);
    }

    public static Specification<Score> hasGradeAtLeast(BigDecimal minGrade) {
        return (root, query, cb) -> 
            minGrade == null ? null :
            cb.greaterThanOrEqualTo(root.get("grade"), minGrade);
    }

}
