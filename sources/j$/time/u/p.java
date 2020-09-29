package j$.time.u;

import j$.time.LocalDate;

/* 'enum' modifier removed */
final class p extends q {
    p(String str, int i) {
        super(str, i, (l) null);
    }

    public G p() {
        return j.YEAR.p();
    }

    public boolean K(w temporal) {
        return temporal.h(j.EPOCH_DAY) && q.a0(temporal);
    }

    public long A(w temporal) {
        if (K(temporal)) {
            return (long) q.X(LocalDate.M(temporal));
        }
        throw new F("Unsupported field: WeekBasedYear");
    }

    public u L(u temporal, long newValue) {
        if (K(temporal)) {
            int newWby = p().a(newValue, q.WEEK_BASED_YEAR);
            LocalDate date = LocalDate.M(temporal);
            int dow = date.i(j.DAY_OF_WEEK);
            int week = q.W(date);
            if (week == 53 && q.Y(newWby) == 52) {
                week = 52;
            }
            LocalDate resolved = LocalDate.a0(newWby, 1, 4);
            return temporal.a(resolved.f0((long) ((dow - resolved.i(j.DAY_OF_WEEK)) + ((week - 1) * 7))));
        }
        throw new F("Unsupported field: WeekBasedYear");
    }

    public String toString() {
        return "WeekBasedYear";
    }
}
