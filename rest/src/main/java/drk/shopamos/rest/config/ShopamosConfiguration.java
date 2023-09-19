package drk.shopamos.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ShopamosConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
