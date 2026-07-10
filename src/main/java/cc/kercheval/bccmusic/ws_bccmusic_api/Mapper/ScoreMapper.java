package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Medley;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Part;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Score;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreComment;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreComposer;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ScoreTag;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.MusicScore;

@Mapper(componentModel = "spring",
        uses = {AccountMapper.class, VendorMapper.class, ArrangementTypeMapper.class, ComposerMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ScoreMapper {

    // --- Entity to DTO ---

    @Mapping(target = "scoreComposers", source = "scoreComposers")
    @Mapping(target = "parts", source = "parts")
    @Mapping(target = "scoreTags", source = "scoreTags")
    @Mapping(target = "medleys", source = "medleys")
    @Mapping(target = "comments", source = "comments")
    MusicScore toDto(Score entity);

    List<MusicScore> toDtoList(List<Score> entities);

    // --- DTO to Entity ---

    @Mapping(target = "scoreComposers", source = "scoreComposers")
    @Mapping(target = "parts", source = "parts")
    @Mapping(target = "scoreTags", source = "scoreTags")
    @Mapping(target = "medleys", source = "medleys")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Score toEntity(MusicScore dto);

    // --- Child mappings (Entity → DTO) ---

    @Mapping(target = "scoreId", source = "score.scoreId")
    @Mapping(target = "composer", source = "composer")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.ScoreComposer scoreComposerToDto(ScoreComposer entity);

    @Mapping(target = "scoreId", source = "score.scoreId")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.Part partToDto(Part entity);

    @Mapping(target = "scoreId", source = "score.scoreId")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.ScoreTag scoreTagToDto(ScoreTag entity);

    @Mapping(target = "scoreId", source = "score.scoreId")
    @Mapping(target = "composer", source = "composer")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.Medley medleyToDto(Medley entity);

    @Mapping(target = "scoreId", source = "score.scoreId")
    @Mapping(target = "createdByAccountId", source = "createdBy.accountId")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "updatedByAccountId", source = "updatedBy.accountId")
    @Mapping(target = "updatedByUsername", source = "updatedBy.username")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.ScoreComment scoreCommentToDto(ScoreComment entity);

    // --- Child mappings (DTO → Entity) ---

    @Mapping(target = "score", ignore = true)
    @Mapping(target = "composer", source = "composer")
    ScoreComposer scoreComposerToEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.ScoreComposer dto);

    @Mapping(target = "score", ignore = true)
    Part partToEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.Part dto);

    @Mapping(target = "score", ignore = true)
    ScoreTag scoreTagToEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.ScoreTag dto);

    @Mapping(target = "score", ignore = true)
    @Mapping(target = "composer", source = "composer")
    Medley medleyToEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.Medley dto);

    @Mapping(target = "score", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ScoreComment scoreCommentToEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.ScoreComment dto);

    // --- Update mappings for child entities ---

    @Mapping(target = "score", ignore = true)
    void updateMedley(@MappingTarget Medley target, Medley source);

    @Mapping(target = "score", ignore = true)
    void updatePart(@MappingTarget Part target, Part source);

    @Mapping(target = "score", ignore = true)
    void updateScoreComposer(@MappingTarget ScoreComposer target, ScoreComposer source);

    @Mapping(target = "score", ignore = true)
    void updateScoreTag(@MappingTarget ScoreTag target, ScoreTag source);

    @Mapping(target = "score", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateScoreComment(@MappingTarget ScoreComment target, ScoreComment source);
}
