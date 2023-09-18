package drk.shopamos.rest.argument;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BadEmailArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        return Stream.of(
                Arguments.of("missingdomain.com"),
                Arguments.of("@example.com"),
                Arguments.of("john.doe@example@com"),
                Arguments.of("jane_doe@.com"),
                Arguments.of(".@domain.com"));
    }
}
