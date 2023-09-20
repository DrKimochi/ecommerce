package drk.shopamos.rest.service;

import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.Account;

import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ServiceTest {

    public static final String MSG_CANNOT_DEMOTE = "error.business.entity.cannot.demote";
    public static final String MSG_CANNOT_DEACTIVATE_ACCOUNT =
            "error.business.entity.cannot.deactivate.account";
    protected static final String MSG_NOT_FOUND_USER = "error.business.entity.notfound.username";
    protected static final String MSG_NOT_FOUND_ID = "error.business.entity.notfound.id";
    protected static final String MSG_EMAIL_EXISTS = "error.business.entity.exists.email";
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext securityContext;

    protected void mockPrincipalAccount(Account account) {
        when(authentication.getPrincipal()).thenReturn(account);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

}
