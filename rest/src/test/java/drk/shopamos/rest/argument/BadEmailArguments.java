package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.ACCOUNT_URI;
import static drk.shopamos.rest.controller.AccountControllerTest.ACCOUNT_URI_WITH_ID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BadEmailArguments implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        return Stream.of(
                Arguments.of(POST, ACCOUNT_URI, null, "missingdomain.com"),
                Arguments.of(POST, ACCOUNT_URI, null, "@example.com"),
                Arguments.of(POST, ACCOUNT_URI, null, "john.doe@example@com"),
                Arguments.of(POST, ACCOUNT_URI, null, "jane_doe@.com"),
                Arguments.of(POST, ACCOUNT_URI, null, ".@domain.com"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "missingdomain.com"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "@example.com"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "john.doe@example@com"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "jane_doe@.com"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", ".@domain.com"));
    }
}
