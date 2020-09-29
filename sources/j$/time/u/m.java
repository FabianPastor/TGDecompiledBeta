package j$.time.u;

import j$.CLASSNAMEk;
import j$.CLASSNAMEm;
import j$.time.LocalDate;
import j$.time.format.I;
import j$.time.t.f;
import j$.time.t.t;
import java.util.Map;

/* 'enum' modifier removed */
final class m extends q {
    m(String str, int i) {
        super(str, i, (l) null);
    }

    public G p() {
        return G.k(1, 90, 92);
    }

    public boolean K(w temporal) {
        return temporal.h(j.DAY_OF_YEAR) && temporal.h(j.MONTH_OF_YEAR) && temporal.h(j.YEAR) && q.a0(temporal);
    }

    public G M(w temporal) {
        if (K(temporal)) {
            long qoy = temporal.f(q.b);
            long j = 91;
            if (qoy == 1) {
                if (!t.a.b0(temporal.f(j.YEAR))) {
                    j = 90;
                }
                return G.j(1, j);
            } else if (qoy == 2) {
                return G.j(1, 91);
            } else {
                if (qoy == 3 || qoy == 4) {
                    return G.j(1, 92);
                }
                return p();
            }
        } else {
            throw new F("Unsupported field: DayOfQuarter");
        }
    }

    public long A(w temporal) {
        if (K(temporal)) {
            return (long) (temporal.i(j.DAY_OF_YEAR) - q.e[((temporal.i(j.MONTH_OF_YEAR) - 1) / 3) + (t.a.b0(temporal.f(j.YEAR)) ? 4 : 0)]);
        }
        throw new F("Unsupported field: DayOfQuarter");
    }

    public u L(u temporal, long newValue) {
        long curValue = A(temporal);
        p().b(newValue, this);
        j jVar = j.DAY_OF_YEAR;
        return temporal.c(jVar, temporal.f(jVar) + (newValue - curValue));
    }

    /* renamed from: b0 */
    public f x(Map fieldValues, w partialTemporal, I resolverStyle) {
        LocalDate date;
        long doq;
        Map map = fieldValues;
        I i = resolverStyle;
        Long yearLong = (Long) map.get(j.YEAR);
        Long qoyLong = (Long) map.get(q.b);
        if (yearLong == null || qoyLong == null) {
            return null;
        }
        int y = j.YEAR.O(yearLong.longValue());
        long doq2 = ((Long) map.get(q.a)).longValue();
        q.V(partialTemporal);
        if (i == I.LENIENT) {
            date = LocalDate.a0(y, 1, 1).g0(CLASSNAMEk.a(CLASSNAMEm.a(qoyLong.longValue(), 1), (long) 3));
            doq = CLASSNAMEm.a(doq2, 1);
        } else {
            LocalDate date2 = LocalDate.a0(y, ((q.b.p().a(qoyLong.longValue(), q.b) - 1) * 3) + 1, 1);
            if (doq2 < 1 || doq2 > 90) {
                if (i == I.STRICT) {
                    M(date2).b(doq2, this);
                } else {
                    p().b(doq2, this);
                }
            }
            doq = doq2 - 1;
            date = date2;
        }
        map.remove(this);
        map.remove(j.YEAR);
        map.remove(q.b);
        return date.f0(doq);
    }

    public String toString() {
        return "DayOfQuarter";
    }
}
