package drk.shopamos.rest.argument;

import static drk.shopamos.rest.mother.CategoryMother.SHIP_CAT_ID;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class ProductFindAllByAttributesArguments implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of(SHIP_CAT_ID, null, null, null, null, null),
                Arguments.of(null, "MerRy", null, null, null, null),
                Arguments.of(null, null, "wOrthy ShIp", null, null, null),
                Arguments.of(null, null, null, BigDecimal.valueOf(10000.01), null, null),
                Arguments.of(null, null, null, null, BigDecimal.valueOf(9998.99), null),
                Arguments.of(null, null, null, null, null, false),
                Arguments.of(SHIP_CAT_ID, "MerRy", "wOrthy ShIp", BigDecimal.valueOf(9998.98), BigDecimal.valueOf(10000.02), true));
    }
}
