package drk.shopamos.rest.mother;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeMother {
    public static final LocalDateTime TODAY =
            Instant.parse("2023-01-01T16:02:42.00Z")
                    .atZone(ZoneId.of("Asia/Calcutta"))
                    .toLocalDateTime();
    public static final LocalDateTime TOMORROW = TODAY.plusDays(1);

    public static final LocalDateTime DECEMBER_2ND_1PM = LocalDateTime.of(2023, 12, 2, 13, 0, 0);
    public static final LocalDateTime NOVEMBER_4TH_3PM = LocalDateTime.of(2023, 11, 4, 15, 0, 0);
}
