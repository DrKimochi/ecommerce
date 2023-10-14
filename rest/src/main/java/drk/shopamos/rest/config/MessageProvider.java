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
    public static final String MSG_NOT_FOUND_ID = "error.service.entity.notfound.id";
    public static final String MSG_NOT_FOUND_USER = "error.service.entity.notfound.username";
    public static final String MSG_EXISTS_EMAIL = "error.service.entity.exists.email";
    public static final String MSG_FORM_FIELD = "error.form.field";
    public static final String MSG_BODY_UNREADABLE = "error.request.body.unreadable";
    public static final String MSG_CANNOT_PROMOTE = "error.forbidden.entity.cannot.promote";
    public static final String MSG_CANNOT_TARGET_OTHERS =
            "error.forbidden.entity.cannot.target.others";
    public static final String MSG_PARAM_WRONG_TYPE = "error.request.param.wrongtype";

    private final Locale locale = LocaleContextHolder.getLocale();
    private final MessageSource messageSource;

    public String getMessage(String msgCode) {
        return messageSource.getMessage(msgCode, null, locale);
    }

    public String getMessage(String msgCode, String... params) {
        return messageSource.getMessage(msgCode, params, locale);
    }

    public String getMessage(String msgCode, Integer param) {
        return messageSource.getMessage(msgCode, new String[] {String.valueOf(param)}, locale);
    }

    public String getMessageWithNamedParams(String msgCode, Map<String, String> namedParams) {
        String resultMessage = getMessage(msgCode);
        for (Map.Entry<String, String> entry : namedParams.entrySet()) {
            resultMessage = resultMessage.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resultMessage;
    }
}
