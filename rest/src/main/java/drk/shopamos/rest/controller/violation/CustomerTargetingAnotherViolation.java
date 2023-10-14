package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.config.MessageProvider.MSG_CANNOT_TARGET_OTHERS;

import drk.shopamos.rest.model.entity.Account;

public class CustomerTargetingAnotherViolation implements PrincipalDataViolation<Account> {

    @Override
    public boolean isViolating(Account principal, Account targetAccount) {
        return !principal.isAdmin() && !principal.getId().equals(targetAccount.getId());
    }

    @Override
    public String getErrorMessageCode() {
        return MSG_CANNOT_TARGET_OTHERS;
    }
}
