package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Collaborator;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface CollaboratorMapper {

    cc.kercheval.bccmusic.ws_bccmusic_api.Model.Collaborator toDto(Collaborator entity);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "collaborator", ignore = true)
    @Mapping(target = "grantedBy", ignore = true)
    Collaborator toEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.Collaborator dto);
}
