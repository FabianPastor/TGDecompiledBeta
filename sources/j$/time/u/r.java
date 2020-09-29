package j$.time.u;

import j$.CLASSNAMEe;
import j$.time.h;

enum r implements E {
    WEEK_BASED_YEARS("WeekBasedYears", h.K(31556952)),
    QUARTER_YEARS("QuarterYears", h.K(7889238));
    
    private final String a;

    private r(String name, h estimatedDuration) {
        this.a = name;
    }

    public boolean i() {
        return true;
    }

    public u p(u temporal, long amount) {
        int i = l.a[ordinal()];
        if (i == 1) {
            B b = s.c;
            return temporal.c(b, CLASSNAMEe.a((long) temporal.i(b), amount));
        } else if (i == 2) {
            return temporal.g(amount / 256, k.YEARS).g((amount % 256) * 3, k.MONTHS);
        } else {
            throw new IllegalStateException("Unreachable");
        }
    }

    public String toString() {
        return this.a;
    }
}
