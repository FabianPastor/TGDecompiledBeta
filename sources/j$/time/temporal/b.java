package j$.time.temporal;
/* loaded from: classes2.dex */
public enum b implements m {
    NANOS("Nanos", j$.time.b.b(1)),
    MICROS("Micros", j$.time.b.b(1000)),
    MILLIS("Millis", j$.time.b.b(1000000)),
    SECONDS("Seconds", j$.time.b.c(1)),
    MINUTES("Minutes", j$.time.b.c(60)),
    HOURS("Hours", j$.time.b.c(3600)),
    HALF_DAYS("HalfDays", j$.time.b.c(43200)),
    DAYS("Days", j$.time.b.c(86400)),
    WEEKS("Weeks", j$.time.b.c(604800)),
    MONTHS("Months", j$.time.b.c(2629746)),
    YEARS("Years", j$.time.b.c(31556952)),
    DECADES("Decades", j$.time.b.c(NUM)),
    CENTURIES("Centuries", j$.time.b.c(3155695200L)),
    MILLENNIA("Millennia", j$.time.b.c(31556952000L)),
    ERAS("Eras", j$.time.b.c(31556952000000000L)),
    FOREVER("Forever", j$.time.b.d(Long.MAX_VALUE, NUM));
    
    private final String a;

    b(String str, j$.time.b bVar) {
        this.a = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.a;
    }
}
