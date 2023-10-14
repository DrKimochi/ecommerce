package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.CREATE_URI;
import static drk.shopamos.rest.controller.AccountControllerTest.GET_DELETE_UPDATE_URI;

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
                Arguments.of(POST, CREATE_URI, null, "missingdomain.com"),
                Arguments.of(POST, CREATE_URI, null, "@example.com"),
                Arguments.of(POST, CREATE_URI, null, "john.doe@example@com"),
                Arguments.of(POST, CREATE_URI, null, "jane_doe@.com"),
                Arguments.of(POST, CREATE_URI, null, ".@domain.com"),
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "123", "missingdomain.com"),
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "123", "@example.com"),
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "123", "john.doe@example@com"),
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "123", "jane_doe@.com"),
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "123", ".@domain.com"));
    }
}
