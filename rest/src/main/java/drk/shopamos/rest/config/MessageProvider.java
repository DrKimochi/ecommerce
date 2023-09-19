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
    public static final String MSG_ENTITY_NOT_FOUND = "error.business.entity.notfound";
    public static final String MSG_ENTITY_EXISTS = "error.business.entity.exists";
    public static final String MSG_FORM_FIELD = "error.form.field";
    public static final String MSG_BODY_UNREADABLE = "error.request.body.unreadable";

    private final Locale locale = LocaleContextHolder.getLocale();
    private final MessageSource messageSource;

    public String getMessage(String msgCode) {
        return messageSource.getMessage(msgCode, null, locale);
    }

    public String getMessage(String msgCode, String param) {
        return messageSource.getMessage(msgCode, new String[] {param}, locale);
    }

    public String getMessageWithNamedParams(String msgCode, Map<String, String> namedParams) {
        String resultMessage = getMessage(msgCode);
        for (Map.Entry<String, String> entry : namedParams.entrySet()) {
            resultMessage = resultMessage.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resultMessage;
    }
}
