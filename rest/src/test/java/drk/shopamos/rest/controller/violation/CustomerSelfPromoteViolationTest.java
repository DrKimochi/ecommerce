package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.NAMI_ID;
import static drk.shopamos.rest.mother.MessageMother.MSG_CANNOT_PROMOTE;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerSelfPromoteViolationTest {

    CustomerSelfPromoteViolation testee = new CustomerSelfPromoteViolation();
    Account principal;
    Account target;

    @BeforeEach
    void setup() {
        principal = new Account();
        principal.setId(NAMI_ID);
        principal.setAdmin(false);
        target = new Account();
        target.setId(NAMI_ID);
        target.setAdmin(true);
    }

    @Test
    @DisplayName("isViolating - When principal has admin role then its not violating")
    void isViolating_whenPrincipalIsAdmin_thenFalse() {
        principal.setAdmin(true);
        assertFalse(testee.isViolating(principal, target));
    }

    @Test
    @DisplayName("isViolating - When target has customer role then its not violating")
    void isViolating_whenTargetIsCustomer_thenFalse() {
        target.setAdmin(false);
        assertFalse(testee.isViolating(principal, target));
    }

    @Test
    @DisplayName("isViolating - When principal is not the target account then its not violating")
    void isViolating_whenPrincipalDifferentFromTarget_thenFalse() {
        target.setId(LUFFY_ID);
        assertFalse(testee.isViolating(principal, target));
    }

    @Test
    @DisplayName(
            "isViolating - When principal is customer and is targeting himself to be promoted then its violating")
    void isViolating_whenCustomerPromotingHimself_thenTrue() {
        assertTrue(testee.isViolating(principal, target));
    }

    @Test
    @DisplayName("getErrorMessageCode - returns correct message code")
    void getErrorMessageCode_returnsCorrectMessageCode() {
        String messageCode = testee.getErrorMessageCode();
        assertThat(messageCode, is(MSG_CANNOT_PROMOTE));
    }
}
