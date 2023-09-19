package drk.shopamos.rest.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MessageProviderTest {
    private static final String MSG_CODE = "msgCode";
    private static final String MSG_PARAM = "msgParam";
    private static final MockedStatic<LocaleContextHolder> localeContextHolder =
            Mockito.mockStatic(LocaleContextHolder.class);
    @Mock private Locale locale;
    @Mock private MessageSource messageSource;

    @AfterAll
    static void tearDown() {
        localeContextHolder.close();
    }

    @Test
    @DisplayName("getMessage - calls MessageSource with correct arguments ")
    void getMessage_callsMessageSourceWithCorrectArguments() {
        localeContextHolder.when(LocaleContextHolder::getLocale).thenReturn(locale);

        MessageProvider testee = new MessageProvider(messageSource);

        testee.getMessage(MSG_CODE, MSG_PARAM);
        verify(messageSource).getMessage(MSG_CODE, new String[] {MSG_PARAM}, locale);

        testee.getMessage(MSG_CODE);
        verify(messageSource).getMessage(MSG_CODE, null, locale);
    }

    @Test
    @DisplayName(
            "getMessageWithNamedParams -replaces named params from raw message returned by messageSource")
    void getMessageWithNamedParams_parsesRawMessage() {
        localeContextHolder.when(LocaleContextHolder::getLocale).thenReturn(locale);
        when(messageSource.getMessage(MSG_CODE, null, locale))
                .thenReturn("This field must be within {min} and {max} characters");

        MessageProvider testee = new MessageProvider(messageSource);

        String message =
                testee.getMessageWithNamedParams(MSG_CODE, Map.of("min", "3", "max", "12"));
        assertThat(message, is("This field must be within 3 and 12 characters"));
    }
}
