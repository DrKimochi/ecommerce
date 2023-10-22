package drk.shopamos.rest.argument;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class CategoryFindAllByAttributesArguments implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of("CeLlA", null),
                Arguments.of(null, "oThEr"),
                Arguments.of("CeLlA", "oThEr"));
    }
}
