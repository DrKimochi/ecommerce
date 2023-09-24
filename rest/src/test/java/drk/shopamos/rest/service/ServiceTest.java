package drk.shopamos.rest.service;

import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.Account;

import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class ServiceTest {

    public static final String MSG_FORM_FIELD = "error.form.field";
    public static final String MSG_FIELD_EMPTY = "error.form.field.empty";
    public static final String MSG_FIELD_EMAIL = "error.form.field.email";
    public static final String MSG_FIELD_PASSWORD = "error.form.field.password";
    public static final String MSG_FIELD_MAX_LENGTH = "error.form.field.maxlength";
    public static final String MSG_BODY_UNREADABLE = "error.request.body.unreadable";
    public static final String MSG_NOT_FOUND_ID = "error.business.entity.notfound.id";
    public static final String MSG_EXISTS_EMAIL = "error.business.entity.exists.email";
    public static final String MSG_PARAM_WRONG_TYPE = "error.request.param.wrongtype";

    @Mock Authentication authentication;
    @Mock SecurityContext securityContext;

    protected void mockPrincipalAccount(Account account) {
        when(authentication.getPrincipal()).thenReturn(account);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
