package drk.shopamos.rest.controller.violation;

import drk.shopamos.rest.controller.exception.IllegalDataException;
import drk.shopamos.rest.model.entity.Account;

import lombok.NonNull;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class PrincipalDataViolationChain<T> {
    private final T targetDataObj;
    private final Account principal;
    private final List<PrincipalDataViolation<T>> principalDataViolationList;

    public PrincipalDataViolationChain(@NonNull T targetDataObj) {
        this.targetDataObj = targetDataObj;
        this.principal =
                (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.principalDataViolationList = new ArrayList<>();
    }

    public PrincipalDataViolationChain<T> add(PrincipalDataViolation<T> principalDataViolation) {
        principalDataViolationList.add(principalDataViolation);
        return this;
    }

    public void verify() {
        this.principalDataViolationList.forEach(
                principalValidation -> {
                    if (principalValidation.isViolating(principal, targetDataObj)) {
                        throw new IllegalDataException(principalValidation.getErrorMessageCode());
                    }
                });
    }
}
