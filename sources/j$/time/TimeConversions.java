package j$.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeConversions {
    private TimeConversions() {
    }

    public static ZonedDateTime convert(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return ZonedDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano(), convert(dateTime.getZone()));
    }

    public static ZonedDateTime convert(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return ZonedDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano(), convert(dateTime.getZone()));
    }

    public static ZoneId convert(ZoneId zoneId) {
        if (zoneId == null) {
            return null;
        }
        return ZoneId.of(zoneId.getId());
    }

    public static ZoneId convert(ZoneId zoneId) {
        if (zoneId == null) {
            return null;
        }
        return ZoneId.of(zoneId.getId());
    }

    public static MonthDay convert(MonthDay monthDay) {
        if (monthDay == null) {
            return null;
        }
        return MonthDay.of(monthDay.getMonthValue(), monthDay.getDayOfMonth());
    }

    public static MonthDay convert(MonthDay monthDay) {
        if (monthDay == null) {
            return null;
        }
        return MonthDay.of(monthDay.getMonthValue(), monthDay.getDayOfMonth());
    }

    public static Instant convert(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Instant.ofEpochSecond(instant.getEpochSecond(), (long) instant.getNano());
    }

    public static Instant convert(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Instant.ofEpochSecond(instant.getEpochSecond(), (long) instant.getNano());
    }

    public static LocalDate convert(LocalDate date) {
        if (date == null) {
            return null;
        }
        return LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static LocalDate convert(LocalDate date) {
        if (date == null) {
            return null;
        }
        return LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static Duration convert(Duration duration) {
        if (duration == null) {
            return null;
        }
        return Duration.ofSeconds(duration.getSeconds(), (long) duration.getNano());
    }

    public static Duration convert(Duration duration) {
        if (duration == null) {
            return null;
        }
        return Duration.ofSeconds(duration.getSeconds(), (long) duration.getNano());
    }
}
