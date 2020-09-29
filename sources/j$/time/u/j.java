package j$.time.u;

public enum j implements B {
    NANO_OF_SECOND("NanoOfSecond", k.NANOS, k.SECONDS, G.j(0, NUM)),
    NANO_OF_DAY("NanoOfDay", k.NANOS, k.DAYS, G.j(0, 86399999999999L)),
    MICRO_OF_SECOND("MicroOfSecond", k.MICROS, k.SECONDS, G.j(0, 999999)),
    MICRO_OF_DAY("MicroOfDay", k.MICROS, k.DAYS, G.j(0, 86399999999L)),
    MILLI_OF_SECOND("MilliOfSecond", k.MILLIS, k.SECONDS, G.j(0, 999)),
    MILLI_OF_DAY("MilliOfDay", k.MILLIS, k.DAYS, G.j(0, 86399999)),
    SECOND_OF_MINUTE("SecondOfMinute", k.SECONDS, k.MINUTES, G.j(0, 59), "second"),
    SECOND_OF_DAY("SecondOfDay", k.SECONDS, k.DAYS, G.j(0, 86399)),
    MINUTE_OF_HOUR("MinuteOfHour", k.MINUTES, k.HOURS, G.j(0, 59), "minute"),
    MINUTE_OF_DAY("MinuteOfDay", k.MINUTES, k.DAYS, G.j(0, 1439)),
    HOUR_OF_AMPM("HourOfAmPm", k.HOURS, k.HALF_DAYS, G.j(0, 11)),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", k.HOURS, k.HALF_DAYS, G.j(1, 12)),
    HOUR_OF_DAY("HourOfDay", k.HOURS, k.DAYS, G.j(0, 23), "hour"),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", k.HOURS, k.DAYS, G.j(1, 24)),
    AMPM_OF_DAY("AmPmOfDay", k.HALF_DAYS, k.DAYS, G.j(0, 1), "dayperiod"),
    DAY_OF_WEEK("DayOfWeek", k.DAYS, k.WEEKS, G.j(1, 7), "weekday"),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", k.DAYS, k.WEEKS, G.j(1, 7)),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", k.DAYS, k.WEEKS, G.j(1, 7)),
    DAY_OF_MONTH("DayOfMonth", k.DAYS, k.MONTHS, G.k(1, 28, 31), "day"),
    DAY_OF_YEAR("DayOfYear", k.DAYS, k.YEARS, G.k(1, 365, 366)),
    EPOCH_DAY("EpochDay", k.DAYS, k.FOREVER, G.j(-365249999634L, 365249999634L)),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", k.WEEKS, k.MONTHS, G.k(1, 4, 5)),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", k.WEEKS, k.YEARS, G.j(1, 53)),
    MONTH_OF_YEAR("MonthOfYear", k.MONTHS, k.YEARS, G.j(1, 12), "month"),
    PROLEPTIC_MONTH("ProlepticMonth", k.MONTHS, k.FOREVER, G.j(-11999999988L, 11999999999L)),
    YEAR_OF_ERA("YearOfEra", k.YEARS, k.FOREVER, G.k(1, NUM, NUM)),
    YEAR("Year", k.YEARS, k.FOREVER, G.j(-NUM, NUM), "year"),
    ERA("Era", k.ERAS, k.FOREVER, G.j(0, 1), "era"),
    INSTANT_SECONDS("InstantSeconds", k.SECONDS, k.FOREVER, G.j(Long.MIN_VALUE, Long.MAX_VALUE)),
    OFFSET_SECONDS("OffsetSeconds", k.SECONDS, k.FOREVER, G.j(-64800, 64800));
    
    private final String a;
    private final G b;

    private j(String name, E baseUnit, E rangeUnit, G range) {
        this.a = name;
        this.b = range;
    }

    private j(String name, E baseUnit, E rangeUnit, G range, String displayNameKey) {
        this.a = name;
        this.b = range;
    }

    public G p() {
        return this.b;
    }

    public boolean i() {
        return ordinal() >= 15 && ordinal() <= 27;
    }

    public boolean r() {
        return ordinal() < 15;
    }

    public long P(long value) {
        p().b(value, this);
        return value;
    }

    public int O(long value) {
        return p().a(value, this);
    }

    public boolean K(w temporal) {
        return temporal.h(this);
    }

    public G M(w temporal) {
        return temporal.p(this);
    }

    public long A(w temporal) {
        return temporal.f(this);
    }

    public u L(u temporal, long newValue) {
        return temporal.c(this, newValue);
    }

    public String toString() {
        return this.a;
    }
}
