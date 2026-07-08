package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer;

@Mapper(componentModel = "spring")
public interface ComposerMapper {

    @Mapping(target = "fullName", expression = "java(buildFullName(entity))")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.Composer toDto(Composer entity);

    @Mapping(target = "fullName", ignore = true)
    Composer toEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.Composer dto);

    default String buildFullName(Composer entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getFirstName() != null) sb.append(entity.getFirstName()).append(" ");
        if (entity.getMiddleName() != null) sb.append(entity.getMiddleName()).append(" ");
        if (entity.getLastName() != null) sb.append(entity.getLastName());
        return sb.toString().trim();
    }
}
