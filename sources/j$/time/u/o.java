package j$.time.u;

import j$.CLASSNAMEm;
import j$.time.LocalDate;
import j$.time.format.I;
import j$.time.t.f;
import java.util.Map;

/* 'enum' modifier removed */
final class o extends q {
    o(String str, int i) {
        super(str, i, (l) null);
    }

    public G p() {
        return G.k(1, 52, 53);
    }

    public boolean K(w temporal) {
        return temporal.h(j.EPOCH_DAY) && q.a0(temporal);
    }

    public G M(w temporal) {
        if (K(temporal)) {
            return q.Z(LocalDate.M(temporal));
        }
        throw new F("Unsupported field: WeekOfWeekBasedYear");
    }

    public long A(w temporal) {
        if (K(temporal)) {
            return (long) q.W(LocalDate.M(temporal));
        }
        throw new F("Unsupported field: WeekOfWeekBasedYear");
    }

    public u L(u temporal, long newValue) {
        p().b(newValue, this);
        return temporal.g(CLASSNAMEm.a(newValue, A(temporal)), k.WEEKS);
    }

    /* renamed from: b0 */
    public f x(Map fieldValues, w partialTemporal, I resolverStyle) {
        LocalDate date;
        long j;
        Map map = fieldValues;
        I i = resolverStyle;
        Long wbyLong = (Long) map.get(q.WEEK_BASED_YEAR);
        Long dowLong = (Long) map.get(j.DAY_OF_WEEK);
        if (wbyLong == null || dowLong == null) {
            return null;
        }
        int wby = q.WEEK_BASED_YEAR.p().a(wbyLong.longValue(), q.WEEK_BASED_YEAR);
        long wowby = ((Long) map.get(q.WEEK_OF_WEEK_BASED_YEAR)).longValue();
        q.V(partialTemporal);
        LocalDate date2 = LocalDate.a0(wby, 1, 4);
        if (i == I.LENIENT) {
            long dow = dowLong.longValue();
            if (dow > 7) {
                date2 = date2.h0((dow - 1) / 7);
                dow = ((dow - 1) % 7) + 1;
                j = 1;
            } else if (dow < 1) {
                date2 = date2.h0(CLASSNAMEm.a(dow, 7) / 7);
                j = 1;
                dow = ((6 + dow) % 7) + 1;
            } else {
                j = 1;
            }
            date = date2.h0(CLASSNAMEm.a(wowby, j)).c(j.DAY_OF_WEEK, dow);
        } else {
            int dow2 = j.DAY_OF_WEEK.O(dowLong.longValue());
            if (wowby < 1 || wowby > 52) {
                if (i == I.STRICT) {
                    q.Z(date2).b(wowby, this);
                } else {
                    p().b(wowby, this);
                }
            }
            date = date2.h0(wowby - 1).c(j.DAY_OF_WEEK, (long) dow2);
        }
        map.remove(this);
        map.remove(q.WEEK_BASED_YEAR);
        map.remove(j.DAY_OF_WEEK);
        return date;
    }

    public String toString() {
        return "WeekOfWeekBasedYear";
    }
}
