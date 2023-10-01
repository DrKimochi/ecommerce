package drk.shopamos.rest.controller.violation;

import static drk.shopamos.rest.mother.AccountMother.buildAdminLuffy;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.controller.exception.IllegalDataException;
import drk.shopamos.rest.model.entity.Account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PrincipalDataViolationChainTest {
    private static final String ERROR_MESSAGE_CODE = "ERROR_MESSAGE_CODE";
    @Mock Authentication authentication;
    @Mock SecurityContext securityContext;
    @Mock AdminSelfDemoteViolation violation;
    @Mock AdminSelfDeactivateViolation anotherViolation;
    Account principal;
    Account targetAccount;
    PrincipalDataViolationChain<Account> testee;

    @BeforeEach
    void setup() {
        principal = buildCustomerNamiWithId();
        targetAccount = buildAdminLuffy();
        mockPrincipalAccount(principal);
        testee = new PrincipalDataViolationChain<>(targetAccount);
    }

    @Test
    @DisplayName("constructor - target object cannot be null")
    void constructor_targetObjCannotBeNull() {
        assertThrows(NullPointerException.class, () -> new PrincipalDataViolationChain<>(null));
    }

    @Test
    @DisplayName(
            "add - Adds a new violation to the list, returns own instance and uses the added violation when verifying violations")
    void add_addsNewViolation_andReturnsSelf() {
        PrincipalDataViolationChain<Account> result = testee.add(violation);
        assertThat(result, is(testee));
        testee.verify();
        Mockito.verify(violation).isViolating(principal, targetAccount);
    }

    @Test
    @DisplayName("verify - calls isViolating for each added violation")
    void verify_callsIsViolatingForEachAddedViolation() {
        testee.add(violation).add(anotherViolation).verify();
        Mockito.verify(violation).isViolating(principal, targetAccount);
        Mockito.verify(anotherViolation).isViolating(principal, targetAccount);
    }

    @Test
    @DisplayName(
            "verify - throws IllegalDataException when isViolating is true for any added violation with its messageErrorCode")
    void verify_throwsIllegalDataException_withIsViolating() {
        when(anotherViolation.isViolating(principal, targetAccount)).thenReturn(true);
        when(anotherViolation.getErrorMessageCode()).thenReturn(ERROR_MESSAGE_CODE);
        testee.add(violation).add(anotherViolation);
        IllegalDataException exception =
                assertThrows(IllegalDataException.class, () -> testee.verify());
        assertThat(exception.getMessageCode(), is(ERROR_MESSAGE_CODE));
    }

    private void mockPrincipalAccount(Account account) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(account);
        SecurityContextHolder.setContext(securityContext);
    }
}
