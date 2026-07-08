package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Vendor;

@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface VendorMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "updatedBy", source = "updatedBy")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.Vendor toDto(Vendor entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Vendor toEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.Vendor dto);
}
