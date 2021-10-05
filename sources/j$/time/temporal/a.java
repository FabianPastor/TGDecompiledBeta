package j$.time.temporal;

public enum a implements k {
    NANO_OF_SECOND("NanoOfSecond", r4, r17, n.c(0, NUM)),
    NANO_OF_DAY("NanoOfDay", r4, r27, n.c(0, 86399999999999L)),
    MICRO_OF_SECOND("MicroOfSecond", r4, r17, n.c(0, 999999)),
    MICRO_OF_DAY("MicroOfDay", r4, r27, n.c(0, 86399999999L)),
    MILLI_OF_SECOND("MilliOfSecond", r4, r17, n.c(0, 999)),
    MILLI_OF_DAY("MilliOfDay", r4, r27, n.c(0, 86399999)),
    SECOND_OF_MINUTE("SecondOfMinute", r17, r32, n.c(0, 59), "second"),
    SECOND_OF_DAY("SecondOfDay", r17, r5, n.c(0, 86399)),
    MINUTE_OF_HOUR("MinuteOfHour", r32, r13, n.c(0, 59), "minute"),
    MINUTE_OF_DAY("MinuteOfDay", r32, r5, n.c(0, 1439)),
    HOUR_OF_AMPM("HourOfAmPm", r13, r16, n.c(0, 11)),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", r4, r16, n.c(1, 12)),
    HOUR_OF_DAY("HourOfDay", r13, r24, n.c(0, 23), "hour"),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", r4, r27, n.c(1, 24)),
    AMPM_OF_DAY("AmPmOfDay", r16, r24, n.c(0, 1), "dayperiod"),
    DAY_OF_WEEK("DayOfWeek", r23, r38, n.c(1, 7), "weekday"),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", r4, r5, n.c(1, 7)),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", r4, r5, n.c(1, 7)),
    DAY_OF_MONTH("DayOfMonth", r23, r15, n.d(1, 28, 31), "day"),
    DAY_OF_YEAR("DayOfYear", r4, r42, n.d(1, 365, 366)),
    EPOCH_DAY("EpochDay", r4, r44, n.c(-365249999634L, 365249999634L)),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", r4, r15, n.d(1, 4, 5)),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", r4, r42, n.c(1, 53)),
    MONTH_OF_YEAR("MonthOfYear", r15, r42, n.c(1, 12), "month"),
    PROLEPTIC_MONTH("ProlepticMonth", r15, r44, n.c(-11999999988L, 11999999999L)),
    YEAR_OF_ERA("YearOfEra", r42, r5, n.d(1, NUM, NUM)),
    YEAR("Year", r42, r24, n.c(-NUM, NUM), "year"),
    ERA("Era", b.ERAS, r24, n.c(0, 1), "era"),
    INSTANT_SECONDS("InstantSeconds", r4, r5, n.c(Long.MIN_VALUE, Long.MAX_VALUE)),
    OFFSET_SECONDS("OffsetSeconds", r4, r5, n.c(-64800, 64800));
    
    private final String a;
    private final n b;

    private a(String str, m mVar, m mVar2, n nVar) {
        this.a = str;
        this.b = nVar;
    }

    private a(String str, m mVar, m mVar2, n nVar, String str2) {
        this.a = str;
        this.b = nVar;
    }

    public n a() {
        return this.b;
    }

    public long b(long j) {
        this.b.a(j, this);
        return j;
    }

    public String toString() {
        return this.a;
    }
}
