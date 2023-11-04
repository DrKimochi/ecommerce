package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.config.MessageProvider.MSG_CANNOT_GET_INACTIVE;

import drk.shopamos.rest.model.entity.Account;

public class CustomerGetInactiveProductViolation implements PrincipalDataViolation<Boolean> {

    @Override
    public boolean isViolating(Account principal, Boolean isActive) {
        return !principal.isAdmin() && Boolean.FALSE.equals(isActive);
    }

    @Override
    public String getErrorMessageCode() {
        return MSG_CANNOT_GET_INACTIVE;
    }
}
