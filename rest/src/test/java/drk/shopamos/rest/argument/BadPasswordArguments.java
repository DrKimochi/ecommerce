package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.ACCOUNT_URI;
import static drk.shopamos.rest.controller.AccountControllerTest.ACCOUNT_URI_WITH_ID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BadPasswordArguments implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        return Stream.of(
                Arguments.of(POST, ACCOUNT_URI, null, "7Letter"),
                Arguments.of(POST, ACCOUNT_URI, null, "uppercase0times"),
                Arguments.of(POST, ACCOUNT_URI, null, "LOWERCASEIS0"),
                Arguments.of(POST, ACCOUNT_URI, null, "NotASingleNumber"),
                Arguments.of(POST, ACCOUNT_URI, null, "PassIsOver16Chars"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "7Letter"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "uppercase0times"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "LOWERCASEIS0"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "NotASingleNumber"),
                Arguments.of(PUT, ACCOUNT_URI_WITH_ID, "123", "PassIsOver16Chars"));
    }
}
