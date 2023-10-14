package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.GET_DELETE_UPDATE_URI;

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
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "2147483648"),
                Arguments.of(PUT, GET_DELETE_UPDATE_URI, "12A"),
                Arguments.of(DELETE, GET_DELETE_UPDATE_URI, "2147483648"),
                Arguments.of(DELETE, GET_DELETE_UPDATE_URI, "12A"),
                Arguments.of(GET, GET_DELETE_UPDATE_URI, "2147483648"),
                Arguments.of(GET, GET_DELETE_UPDATE_URI, "12A"));
    }
}
