package drk.shopamos.rest.argument;

import static drk.shopamos.rest.controller.AccountControllerTest.ACCOUNT_URI_WITH_ID;

import static drk.shopamos.rest.controller.ProductControllerTest.PRODUCT_URI;
import static drk.shopamos.rest.controller.ProductControllerTest.PRODUCT_URI_WITH_ID;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class ProductCreateUpdateDeleteUriArguments implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of(PUT, PRODUCT_URI_WITH_ID, 1),
                Arguments.of(PUT, PRODUCT_URI_WITH_ID, 1),
                Arguments.of(DELETE, PRODUCT_URI_WITH_ID, 1),
                Arguments.of(DELETE, PRODUCT_URI_WITH_ID, 1),
                Arguments.of(POST, PRODUCT_URI, null),
                Arguments.of(POST, PRODUCT_URI, null));
    }
}
