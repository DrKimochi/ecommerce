package drk.shopamos.rest.mother;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeMother {

    public static final Instant TODAY_INSTANT = Instant.parse("2023-01-01T16:02:42.00Z");
    public static final Instant TOMORROW_INSTANT = Instant.parse("2023-01-02T16:02:42.00Z");
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Calcutta");
    public static final LocalDateTime TODAY = TODAY_INSTANT.atZone(ZONE_ID).toLocalDateTime();
    public static final LocalDateTime TOMORROW = TODAY.plusDays(1);

    public static final LocalDateTime DECEMBER_2ND_1PM = LocalDateTime.of(2023, 12, 2, 13, 0, 0);
    public static final LocalDateTime NOVEMBER_4TH_3PM = LocalDateTime.of(2023, 11, 4, 15, 0, 0);
}
