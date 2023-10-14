package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.ACCOUNT_URI_WITH_ID;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class AccountGetDeleteUpdateUriArguments implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "2147483648"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "12A"),
                Arguments.of(DELETE, ACCOUNT_URI_WITH_ID, "2147483648"),
                Arguments.of(DELETE, ACCOUNT_URI_WITH_ID, "12A"),
                Arguments.of(GET, ACCOUNT_URI_WITH_ID, "2147483648"),
                Arguments.of(GET, ACCOUNT_URI_WITH_ID, "12A"));
    }
}
