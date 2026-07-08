package cc.kercheval.bccmusic.ws_bccmusic_api.Mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Account;
import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.AccountInfo;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AccountMapper {

    @Mapping(target = "accountType", source = "accountType", qualifiedByName = "roleToString")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    cc.kercheval.bccmusic.ws_bccmusic_api.Model.Account toDto(Account entity);

    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "hashedPassword", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Account toEntity(cc.kercheval.bccmusic.ws_bccmusic_api.Model.Account dto);

    AccountInfo toAccountInfo(Account entity);

    @Named("roleToString")
    default String roleToString(Role role) {
        return role == null ? null : role.name();
    }
}
