package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import org.mapstruct.Mapper;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ArrangementType;

@Mapper(componentModel = "spring")
public interface ArrangementTypeMapper {

    cc.kercheval.bccmusic.ws_bccmusic_api.Model.ArrangementType toDto(ArrangementType entity);

    ArrangementType toEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.ArrangementType dto);
}
