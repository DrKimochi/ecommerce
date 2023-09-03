package drk.shopamos.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.ZoneId;

@SpringBootTest
class RestApplicationTests {
    @Autowired Clock clock;

    @Test
    @DisplayName("When clock is instantiated then it is using system default zone")
    void clock_isUsingSystemDefaultZone() {
        assertThat(clock.getZone(), is(ZoneId.systemDefault()));
    }
}
