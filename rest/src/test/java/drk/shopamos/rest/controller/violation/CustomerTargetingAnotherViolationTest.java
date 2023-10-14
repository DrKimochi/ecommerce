package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.NAMI_ID;
import static drk.shopamos.rest.mother.MessageMother.MSG_CANNOT_TARGET_OTHERS;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerTargetingAnotherViolationTest {

    CustomerTargetingAnotherViolation customerTargetingAnotherViolation =
            new CustomerTargetingAnotherViolation();
    Account principal;
    Account target;

    @BeforeEach
    void setup() {
        principal = new Account();
        principal.setId(NAMI_ID);
        target = new Account();
        target.setId(LUFFY_ID);
    }

    @Test
    @DisplayName(
            "isViolating - When principal is customer but is the target account then its not violating")
    void isViolating_whenPrincipalSameAsTarget_thenFalse() {
        target.setId(NAMI_ID);
        assertFalse(customerTargetingAnotherViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName("isViolating - When principal is admin then its not violating")
    void isViolating_whenPrincipalIsAdmin_thenFalse() {
        principal.setAdmin(true);
        assertFalse(customerTargetingAnotherViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName(
            "isViolating - When principal is customer targeting other account then its violating")
    void isViolating_whenPrincipalIsCustomerTargetingOther_thenTrue() {
        assertTrue(customerTargetingAnotherViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName("getErrorMessageCode - returns correct message code")
    void getErrorMessageCode_returnsCorrectMessageCode() {
        String messageCode = customerTargetingAnotherViolation.getErrorMessageCode();
        assertThat(messageCode, is(MSG_CANNOT_TARGET_OTHERS));
    }
}
