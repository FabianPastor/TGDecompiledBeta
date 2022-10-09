package j$.time.temporal;
/* JADX WARN: Init of enum ALIGNED_DAY_OF_WEEK_IN_MONTH can be incorrect */
/* JADX WARN: Init of enum ALIGNED_DAY_OF_WEEK_IN_YEAR can be incorrect */
/* JADX WARN: Init of enum ALIGNED_WEEK_OF_MONTH can be incorrect */
/* JADX WARN: Init of enum ALIGNED_WEEK_OF_YEAR can be incorrect */
/* JADX WARN: Init of enum AMPM_OF_DAY can be incorrect */
/* JADX WARN: Init of enum CLOCK_HOUR_OF_AMPM can be incorrect */
/* JADX WARN: Init of enum CLOCK_HOUR_OF_DAY can be incorrect */
/* JADX WARN: Init of enum DAY_OF_MONTH can be incorrect */
/* JADX WARN: Init of enum DAY_OF_WEEK can be incorrect */
/* JADX WARN: Init of enum DAY_OF_YEAR can be incorrect */
/* JADX WARN: Init of enum EPOCH_DAY can be incorrect */
/* JADX WARN: Init of enum ERA can be incorrect */
/* JADX WARN: Init of enum HOUR_OF_AMPM can be incorrect */
/* JADX WARN: Init of enum HOUR_OF_DAY can be incorrect */
/* JADX WARN: Init of enum INSTANT_SECONDS can be incorrect */
/* JADX WARN: Init of enum MICRO_OF_DAY can be incorrect */
/* JADX WARN: Init of enum MICRO_OF_SECOND can be incorrect */
/* JADX WARN: Init of enum MILLI_OF_DAY can be incorrect */
/* JADX WARN: Init of enum MILLI_OF_SECOND can be incorrect */
/* JADX WARN: Init of enum MINUTE_OF_DAY can be incorrect */
/* JADX WARN: Init of enum MINUTE_OF_HOUR can be incorrect */
/* JADX WARN: Init of enum MONTH_OF_YEAR can be incorrect */
/* JADX WARN: Init of enum NANO_OF_DAY can be incorrect */
/* JADX WARN: Init of enum NANO_OF_SECOND can be incorrect */
/* JADX WARN: Init of enum OFFSET_SECONDS can be incorrect */
/* JADX WARN: Init of enum PROLEPTIC_MONTH can be incorrect */
/* JADX WARN: Init of enum SECOND_OF_DAY can be incorrect */
/* JADX WARN: Init of enum SECOND_OF_MINUTE can be incorrect */
/* JADX WARN: Init of enum YEAR can be incorrect */
/* JADX WARN: Init of enum YEAR_OF_ERA can be incorrect */
/* loaded from: classes2.dex */
public enum a implements k {
    NANO_OF_SECOND("NanoOfSecond", r8, r17, n.c(0, NUM)),
    NANO_OF_DAY("NanoOfDay", r8, r27, n.c(0, 86399999999999L)),
    MICRO_OF_SECOND("MicroOfSecond", r9, r17, n.c(0, 999999)),
    MICRO_OF_DAY("MicroOfDay", r9, r27, n.c(0, 86399999999L)),
    MILLI_OF_SECOND("MilliOfSecond", r9, r17, n.c(0, 999)),
    MILLI_OF_DAY("MilliOfDay", r9, r27, n.c(0, 86399999)),
    SECOND_OF_MINUTE("SecondOfMinute", r17, r32, n.c(0, 59), "second"),
    SECOND_OF_DAY("SecondOfDay", r17, r27, n.c(0, 86399)),
    MINUTE_OF_HOUR("MinuteOfHour", r32, r13, n.c(0, 59), "minute"),
    MINUTE_OF_DAY("MinuteOfDay", r32, r27, n.c(0, 1439)),
    HOUR_OF_AMPM("HourOfAmPm", r13, r16, n.c(0, 11)),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", r13, r16, n.c(1, 12)),
    HOUR_OF_DAY("HourOfDay", r13, r27, n.c(0, 23), "hour"),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", r13, r27, n.c(1, 24)),
    AMPM_OF_DAY("AmPmOfDay", r16, r27, n.c(0, 1), "dayperiod"),
    DAY_OF_WEEK("DayOfWeek", r27, r38, n.c(1, 7), "weekday"),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", r27, r38, n.c(1, 7)),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", r27, r38, n.c(1, 7)),
    DAY_OF_MONTH("DayOfMonth", r27, r15, n.d(1, 28, 31), "day"),
    DAY_OF_YEAR("DayOfYear", r27, r42, n.d(1, 365, 366)),
    EPOCH_DAY("EpochDay", r27, r44, n.c(-365249999634L, 365249999634L)),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", r38, r15, n.d(1, 4, 5)),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", r38, r42, n.c(1, 53)),
    MONTH_OF_YEAR("MonthOfYear", r15, r42, n.c(1, 12), "month"),
    PROLEPTIC_MONTH("ProlepticMonth", r15, r44, n.c(-11999999988L, 11999999999L)),
    YEAR_OF_ERA("YearOfEra", r42, r44, n.d(1, NUM, NUM)),
    YEAR("Year", r42, r44, n.c(-NUM, NUM), "year"),
    ERA("Era", b.ERAS, r44, n.c(0, 1), "era"),
    INSTANT_SECONDS("InstantSeconds", r17, r44, n.c(Long.MIN_VALUE, Long.MAX_VALUE)),
    OFFSET_SECONDS("OffsetSeconds", r17, r44, n.c(-64800, 64800));
    
    private final String a;
    private final n b;

    static {
        b bVar = b.NANOS;
        b bVar2 = b.SECONDS;
        b bVar3 = b.DAYS;
        b bVar4 = b.MICROS;
        b bVar5 = b.MILLIS;
        b bVar6 = b.MINUTES;
        b bVar7 = b.HOURS;
        b bVar8 = b.HALF_DAYS;
        b bVar9 = b.WEEKS;
        b bVar10 = b.MONTHS;
        b bVar11 = b.YEARS;
        b bVar12 = b.FOREVER;
    }

    a(String str, m mVar, m mVar2, n nVar) {
        this.a = str;
        this.b = nVar;
    }

    a(String str, m mVar, m mVar2, n nVar, String str2) {
        this.a = str;
        this.b = nVar;
    }

    @Override // j$.time.temporal.k
    public n a() {
        return this.b;
    }

    public long b(long j) {
        this.b.a(j, this);
        return j;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.a;
    }
}
