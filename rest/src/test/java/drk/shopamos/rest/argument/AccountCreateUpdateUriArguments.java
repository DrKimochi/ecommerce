package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.CREATE_URI;
import static drk.shopamos.rest.controller.AccountControllerTest.UPDATE_URI;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class AccountCreateUpdateUriArguments implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of(POST, CREATE_URI, null), Arguments.of(PUT, UPDATE_URI, "123"));
    }
}
