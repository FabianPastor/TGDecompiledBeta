package j$.time.temporal;
/* loaded from: classes2.dex */
enum i implements m {
    WEEK_BASED_YEARS("WeekBasedYears", j$.time.b.c(31556952)),
    QUARTER_YEARS("QuarterYears", j$.time.b.c(7889238));
    
    private final String a;

    i(String str, j$.time.b bVar) {
        this.a = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.a;
    }
}
