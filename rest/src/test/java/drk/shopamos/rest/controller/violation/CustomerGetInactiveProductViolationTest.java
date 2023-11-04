package drk.shopamos.rest.controller.violation;

import static org.junit.jupiter.api.Assertions.*;

import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CustomerGetInactiveProductViolationTest {
    CustomerGetInactiveProductViolation testee = new CustomerGetInactiveProductViolation();
    Account principal = new Account();

    @ParameterizedTest
    @NullSource
    @ValueSource(booleans = {true, false})
    @DisplayName("isViolating - When principal has admin role then it is not violating")
    void isViolating_whenPrincipalIsAdmin_thenNotViolating(Boolean isActive) {
        principal.setAdmin(true);
        assertFalse(testee.isViolating(principal, isActive));
    }

    @Test
    @DisplayName(
            "isViolating - When principal has customer role and isActive is false then it is  violating")
    void isViolating_whenPrincipalIsCustomer_andIsActiveIsTrue_thenNotViolating() {
        principal.setAdmin(false);
        assertTrue(testee.isViolating(principal, Boolean.FALSE));
    }
}
