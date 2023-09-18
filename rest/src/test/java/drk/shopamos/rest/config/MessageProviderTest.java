package drk.shopamos.rest.config;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

@ExtendWith(MockitoExtension.class)
class MessageProviderTest {
    private static final String MSG_CODE = "msgCode";
    private static final String MSG_ARG = "msgArg";
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

        testee.getMessage(MSG_CODE, MSG_ARG);
        verify(messageSource).getMessage(MSG_CODE, new String[] {MSG_ARG}, locale);

        testee.getMessage(MSG_CODE);
        verify(messageSource).getMessage(MSG_CODE, null, locale);
    }
}
