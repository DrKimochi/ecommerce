package drk.shopamos.rest.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageProvider {
    public static final String MSG_NOT_FOUND_ID = "error.business.entity.notfound.id";
    public static final String MSG_NOT_FOUND_USER = "error.business.entity.notfound.username";
    public static final String MSG_EXISTS_EMAIL = "error.business.entity.exists.email";
    public static final String MSG_FORM_FIELD = "error.form.field";
    public static final String MSG_BODY_UNREADABLE = "error.request.body.unreadable";
    public static final String MSG_CANNOT_DEMOTE = "error.business.entity.cannot.demote";
    public static final String MSG_CANNOT_DEACTIVATE_ACCOUNT =
            "error.business.entity.cannot.deactivate.account";

    private final Locale locale = LocaleContextHolder.getLocale();
    private final MessageSource messageSource;

    public String getMessage(String msgCode) {
        return messageSource.getMessage(msgCode, null, locale);
    }

    public String getMessage(String msgCode, Object param) {
        return messageSource.getMessage(msgCode, new Object[] {param}, locale);
    }

    public String getMessageWithNamedParams(String msgCode, Map<String, String> namedParams) {
        String resultMessage = getMessage(msgCode);
        for (Map.Entry<String, String> entry : namedParams.entrySet()) {
            resultMessage = resultMessage.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resultMessage;
    }
}
