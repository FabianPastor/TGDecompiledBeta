package j$.time.u;

import j$.time.h;

public enum k implements E {
    NANOS("Nanos", h.A(1)),
    MICROS("Micros", h.A(1000)),
    MILLIS("Millis", h.A(1000000)),
    SECONDS("Seconds", h.K(1)),
    MINUTES("Minutes", h.K(60)),
    HOURS("Hours", h.K(3600)),
    HALF_DAYS("HalfDays", h.K(43200)),
    DAYS("Days", h.K(86400)),
    WEEKS("Weeks", h.K(604800)),
    MONTHS("Months", h.K(2629746)),
    YEARS("Years", h.K(31556952)),
    DECADES("Decades", h.K(NUM)),
    CENTURIES("Centuries", h.K(3155695200L)),
    MILLENNIA("Millennia", h.K(31556952000L)),
    ERAS("Eras", h.K(31556952000000000L)),
    FOREVER("Forever", h.L(Long.MAX_VALUE, NUM));
    
    private final String a;

    private k(String name, h estimatedDuration) {
        this.a = name;
    }

    public boolean i() {
        return compareTo(DAYS) >= 0 && this != FOREVER;
    }

    public u p(u temporal, long amount) {
        return temporal.g(amount, this);
    }

    public String toString() {
        return this.a;
    }
}
