package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.AccountUpgradeRequest;

@Mapper(componentModel = "spring")
public interface AccountUpgradeRequestMapper {

    @Mapping(target = "accountId", source = "account.accountId")
    @Mapping(target = "accountName", source = "account.accountName")
    @Mapping(target = "resolvedByUsername", source = "resolvedBy.username")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.AccountUpgradeRequest toDto(AccountUpgradeRequest entity);
}
