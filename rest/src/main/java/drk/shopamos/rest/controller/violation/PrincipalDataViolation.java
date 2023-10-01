package drk.shopamos.rest.controller.violation;

import drk.shopamos.rest.model.entity.Account;

public interface PrincipalDataViolation<T> {

    boolean isViolating(Account principal, T targetDataObj);

    String getErrorMessageCode();
}
