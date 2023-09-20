package drk.shopamos.rest.controller.mapper;

import drk.shopamos.rest.controller.request.AccountRequest;
import drk.shopamos.rest.model.entity.Account;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountMapper {

    @Mapping(target = "authorities", ignore = true)
    Account map(AccountRequest accountRequest, Integer id);

    default Account map(AccountRequest accountRequest) {
        return map(accountRequest, null);
    }
}
