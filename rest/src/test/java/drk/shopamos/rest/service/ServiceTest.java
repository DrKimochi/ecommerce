package drk.shopamos.rest.service;

import static drk.shopamos.rest.mother.MessageMother.RETURNED_MSG;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.config.MessageProvider;

import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

public abstract class ServiceTest {
    @Mock MessageProvider messageProvider;

    protected void assertException(
            Class<? extends Throwable> expectedType, Executable executable, String messageCode) {
        when(messageProvider.getMessage(messageCode)).thenReturn(RETURNED_MSG);
        String exceptionMessage = assertThrows(expectedType, executable).getMessage();
        assertThat(exceptionMessage, is(RETURNED_MSG));
    }

    protected void assertException(
            Class<? extends Throwable> expectedType,
            Executable executable,
            String messageCode,
            Integer messageParam) {
        when(messageProvider.getMessage(messageCode, messageParam)).thenReturn(RETURNED_MSG);
        String exceptionMessage = assertThrows(expectedType, executable).getMessage();
        assertThat(exceptionMessage, is(RETURNED_MSG));
    }

    protected void assertException(
            Class<? extends Throwable> expectedType,
            Executable executable,
            String messageCode,
            String... messageParams) {
        when(messageProvider.getMessage(messageCode, messageParams)).thenReturn(RETURNED_MSG);
        String exceptionMessage = assertThrows(expectedType, executable).getMessage();
        assertThat(exceptionMessage, is(RETURNED_MSG));
    }
}
