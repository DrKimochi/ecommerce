package drk.shopamos.rest.argument;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BadPasswordArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        return Stream.of(
                Arguments.of("7Letter"),
                Arguments.of("uppercase0times"),
                Arguments.of("LOWERCASEIS0"),
                Arguments.of("NotASingleNumber"),
                Arguments.of("PassIsOver16Chars"));
    }
}
