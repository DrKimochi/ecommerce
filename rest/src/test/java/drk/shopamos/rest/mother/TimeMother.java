package drk.shopamos.rest.mother;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeMother {
    public static final LocalDateTime TODAY = Instant.parse("2023-01-01T16:02:42.00Z").atZone(ZoneId.of("Asia/Calcutta")).toLocalDateTime();
    public static final LocalDateTime TOMORROW = TODAY.plusDays(1);
}
