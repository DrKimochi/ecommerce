package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.config.MessageProvider.MSG_CANNOT_DEACTIVATE;

import drk.shopamos.rest.model.entity.Account;

public class AdminSelfDeactivateViolation implements PrincipalDataViolation<Account> {

    @Override
    public boolean isViolating(Account principal, Account targetAccount) {
        return principal.isAdmin()
                && !targetAccount.isActive()
                && principal.getId().equals(targetAccount.getId());
    }

    @Override
    public String getErrorMessageCode() {
        return MSG_CANNOT_DEACTIVATE;
    }
}
