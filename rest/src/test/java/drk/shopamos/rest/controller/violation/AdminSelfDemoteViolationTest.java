package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.mother.AccountMother.LUFFY_ID;
import static drk.shopamos.rest.mother.AccountMother.NAMI_ID;
import static drk.shopamos.rest.mother.MessageMother.MSG_CANNOT_DEMOTE;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdminSelfDemoteViolationTest {
    AdminSelfDemoteViolation adminSelfDemoteViolation = new AdminSelfDemoteViolation();
    Account principal;
    Account target;

    @BeforeEach
    void setup() {
        principal = new Account();
        principal.setId(NAMI_ID);
        principal.setAdmin(true);
        target = new Account();
        target.setId(NAMI_ID);
        target.setAdmin(false);
    }

    @Test
    @DisplayName("isViolating - When target account has admin role then its not violating")
    void isViolating_whenTargetAccountIsAdmin_thenFalse() {
        target.setAdmin(true);
        assertFalse(adminSelfDemoteViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName("isViolating - When principal has customer role then ita not violating")
    void isViolating_whenPrincipalIsCustomer_thenFalse() {
        principal.setAdmin(false);
        assertFalse(adminSelfDemoteViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName("isViolating - When principal is not the target account then its not violating")
    void isViolating_whenPrincipalDifferentFromTarget_thenFalse() {
        target.setId(LUFFY_ID);
        assertFalse(adminSelfDemoteViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName(
            "isViolating - When principal is admin and is targeting himself to be demoted then it is violating")
    void isViolating_whenAdminDemotingHimself_thenTrue() {
        assertTrue(adminSelfDemoteViolation.isViolating(principal, target));
    }

    @Test
    @DisplayName("getErrorMessageCode - returns correct message code")
    void getErrorMessageCode_returnsCorrectMessageCode() {
        String messageCode = adminSelfDemoteViolation.getErrorMessageCode();
        assertThat(messageCode, is(MSG_CANNOT_DEMOTE));
    }
}
