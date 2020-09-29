package j$.time.u;

import j$.CLASSNAMEd;
import j$.CLASSNAMEe;
import j$.CLASSNAMEh;
import j$.CLASSNAMEk;
import j$.CLASSNAMEm;
import j$.time.format.I;
import j$.time.t.f;
import j$.time.t.p;
import j$.time.t.q;
import java.util.Map;

class H implements B {
    private static final G f = G.j(1, 7);
    private static final G g = G.l(0, 1, 4, 6);
    private static final G h = G.l(0, 1, 52, 54);
    private static final G i = G.k(1, 52, 53);
    private final String a;
    private final I b;
    private final E c;
    private final E d;
    private final G e;

    static H h(I weekDef) {
        return new H("DayOfWeek", weekDef, k.DAYS, k.WEEKS, f);
    }

    static H l(I weekDef) {
        return new H("WeekOfMonth", weekDef, k.WEEKS, k.MONTHS, g);
    }

    static H n(I weekDef) {
        return new H("WeekOfYear", weekDef, k.WEEKS, k.YEARS, h);
    }

    static H m(I weekDef) {
        return new H("WeekOfWeekBasedYear", weekDef, k.WEEKS, s.d, i);
    }

    static H k(I weekDef) {
        return new H("WeekBasedYear", weekDef, s.d, k.FOREVER, j.YEAR.p());
    }

    private f j(q chrono, int yowby, int wowby, int dow) {
        f date = chrono.C(yowby, 1, 1);
        int offset = w(1, c(date));
        return date.g((long) ((-offset) + (dow - 1) + ((Math.min(wowby, a(offset, this.b.f() + date.lengthOfYear()) - 1) - 1) * 7)), k.DAYS);
    }

    private H(String name, I weekDef, E baseUnit, E rangeUnit, G range) {
        this.a = name;
        this.b = weekDef;
        this.c = baseUnit;
        this.d = rangeUnit;
        this.e = range;
    }

    public long A(w temporal) {
        E e2 = this.d;
        if (e2 == k.WEEKS) {
            return (long) c(temporal);
        }
        if (e2 == k.MONTHS) {
            return e(temporal);
        }
        if (e2 == k.YEARS) {
            return g(temporal);
        }
        if (e2 == I.h) {
            return (long) f(temporal);
        }
        if (e2 == k.FOREVER) {
            return (long) d(temporal);
        }
        throw new IllegalStateException("unreachable, rangeUnit: " + this.d + ", this: " + this);
    }

    private int c(w temporal) {
        return CLASSNAMEh.a(temporal.i(j.DAY_OF_WEEK) - this.b.e().getValue(), 7) + 1;
    }

    private int b(int isoDow) {
        return CLASSNAMEh.a(isoDow - this.b.e().getValue(), 7) + 1;
    }

    private long e(w temporal) {
        int dow = c(temporal);
        int dom = temporal.i(j.DAY_OF_MONTH);
        return (long) a(w(dom, dow), dom);
    }

    private long g(w temporal) {
        int dow = c(temporal);
        int doy = temporal.i(j.DAY_OF_YEAR);
        return (long) a(w(doy, dow), doy);
    }

    private int d(w temporal) {
        int dow = c(temporal);
        int year = temporal.i(j.YEAR);
        int doy = temporal.i(j.DAY_OF_YEAR);
        int offset = w(doy, dow);
        int week = a(offset, doy);
        if (week == 0) {
            return year - 1;
        }
        if (week >= a(offset, this.b.f() + ((int) temporal.p(j.DAY_OF_YEAR).d()))) {
            return year + 1;
        }
        return year;
    }

    private int f(w temporal) {
        int dow = c(temporal);
        int doy = temporal.i(j.DAY_OF_YEAR);
        int offset = w(doy, dow);
        int week = a(offset, doy);
        if (week == 0) {
            return f(p.e(temporal).o(temporal).I((long) doy, k.DAYS));
        }
        if (week <= 50) {
            return week;
        }
        int newYearWeek = a(offset, this.b.f() + ((int) temporal.p(j.DAY_OF_YEAR).d()));
        if (week >= newYearWeek) {
            return (week - newYearWeek) + 1;
        }
        return week;
    }

    private int w(int day, int dow) {
        int weekStart = CLASSNAMEh.a(day - dow, 7);
        int offset = -weekStart;
        if (weekStart + 1 > this.b.f()) {
            return 7 - weekStart;
        }
        return offset;
    }

    private int a(int offset, int day) {
        return ((offset + 7) + (day - 1)) / 7;
    }

    public u L(u temporal, long newValue) {
        int newVal = this.e.a(newValue, this);
        int currentVal = temporal.i(this);
        if (newVal == currentVal) {
            return temporal;
        }
        if (this.d != k.FOREVER) {
            return temporal.g((long) (newVal - currentVal), this.c);
        }
        int idow = temporal.i(this.b.c);
        return j(p.e(temporal), (int) newValue, temporal.i(this.b.e), idow);
    }

    /* renamed from: s */
    public f x(Map fieldValues, w partialTemporal, I resolverStyle) {
        int dow;
        q chrono;
        Map map = fieldValues;
        long value = ((Long) map.get(this)).longValue();
        int newValue = CLASSNAMEd.a(value);
        if (this.d == k.WEEKS) {
            int checkedValue = this.e.a(value, this);
            map.remove(this);
            map.put(j.DAY_OF_WEEK, Long.valueOf((long) (CLASSNAMEh.a((this.b.e().getValue() - 1) + (checkedValue - 1), 7) + 1)));
            return null;
        } else if (!map.containsKey(j.DAY_OF_WEEK)) {
            return null;
        } else {
            j jVar = j.DAY_OF_WEEK;
            int dow2 = b(jVar.O(((Long) map.get(jVar)).longValue()));
            q chrono2 = p.e(partialTemporal);
            if (map.containsKey(j.YEAR)) {
                j jVar2 = j.YEAR;
                int year = jVar2.O(((Long) map.get(jVar2)).longValue());
                if (this.d != k.MONTHS || !map.containsKey(j.MONTH_OF_YEAR)) {
                    chrono = chrono2;
                    dow = dow2;
                    if (this.d == k.YEARS) {
                        return v(fieldValues, chrono, year, (long) newValue, dow, resolverStyle);
                    }
                } else {
                    q qVar = chrono2;
                    int i2 = dow2;
                    return u(fieldValues, chrono2, year, ((Long) map.get(j.MONTH_OF_YEAR)).longValue(), (long) newValue, dow2, resolverStyle);
                }
            } else {
                chrono = chrono2;
                dow = dow2;
                E e2 = this.d;
                if (e2 == I.h || e2 == k.FOREVER) {
                    if (!map.containsKey(this.b.f)) {
                        I i3 = resolverStyle;
                        q qVar2 = chrono;
                        int i4 = dow;
                    } else if (map.containsKey(this.b.e)) {
                        return t(map, chrono, dow, resolverStyle);
                    } else {
                        I i5 = resolverStyle;
                        q qVar3 = chrono;
                        int i6 = dow;
                    }
                    return null;
                }
            }
            I i7 = resolverStyle;
            q qVar4 = chrono;
            int i8 = dow;
            return null;
        }
    }

    private f u(Map fieldValues, q chrono, int year, long month, long wom, int localDow, I resolverStyle) {
        f date;
        Map map = fieldValues;
        q qVar = chrono;
        int i2 = year;
        long j = month;
        long j2 = wom;
        I i3 = resolverStyle;
        if (i3 == I.LENIENT) {
            f date2 = qVar.C(i2, 1, 1).g(CLASSNAMEm.a(j, 1), k.MONTHS);
            long weeks = CLASSNAMEm.a(j2, e(date2));
            long j3 = weeks;
            date = date2.g(CLASSNAMEe.a(CLASSNAMEk.a(weeks, (long) 7), (long) (localDow - c(date2))), k.DAYS);
        } else {
            f date3 = qVar.C(i2, j.MONTH_OF_YEAR.O(j), 1);
            f date4 = date3.g((long) ((((int) (((long) this.e.a(j2, this)) - e(date3))) * 7) + (localDow - c(date3))), k.DAYS);
            if (i3 != I.STRICT || date4.f(j.MONTH_OF_YEAR) == j) {
                date = date4;
            } else {
                throw new j$.time.f("Strict mode rejected resolved date as it is in a different month");
            }
        }
        map.remove(this);
        map.remove(j.YEAR);
        map.remove(j.MONTH_OF_YEAR);
        map.remove(j.DAY_OF_WEEK);
        return date;
    }

    private f v(Map fieldValues, q chrono, int year, long woy, int localDow, I resolverStyle) {
        f date;
        Map map = fieldValues;
        int i2 = year;
        long j = woy;
        I i3 = resolverStyle;
        f date2 = chrono.C(i2, 1, 1);
        if (i3 == I.LENIENT) {
            date = date2.g(CLASSNAMEe.a(CLASSNAMEk.a(CLASSNAMEm.a(j, g(date2)), (long) 7), (long) (localDow - c(date2))), k.DAYS);
        } else {
            date = date2.g((long) ((((int) (((long) this.e.a(j, this)) - g(date2))) * 7) + (localDow - c(date2))), k.DAYS);
            if (i3 == I.STRICT && date.f(j.YEAR) != ((long) i2)) {
                throw new j$.time.f("Strict mode rejected resolved date as it is in a different year");
            }
        }
        map.remove(this);
        map.remove(j.YEAR);
        map.remove(j.DAY_OF_WEEK);
        return date;
    }

    private f t(Map fieldValues, q chrono, int localDow, I resolverStyle) {
        f date;
        int yowby = this.b.f.p().a(((Long) fieldValues.get(this.b.f)).longValue(), this.b.f);
        if (resolverStyle == I.LENIENT) {
            date = j(chrono, yowby, 1, localDow).g(CLASSNAMEm.a(((Long) fieldValues.get(this.b.e)).longValue(), 1), k.WEEKS);
        } else {
            f date2 = j(chrono, yowby, this.b.e.p().a(((Long) fieldValues.get(this.b.e)).longValue(), this.b.e), localDow);
            if (resolverStyle != I.STRICT || d(date2) == yowby) {
                date = date2;
            } else {
                throw new j$.time.f("Strict mode rejected resolved date as it is in a different week-based-year");
            }
        }
        fieldValues.remove(this);
        fieldValues.remove(this.b.f);
        fieldValues.remove(this.b.e);
        fieldValues.remove(j.DAY_OF_WEEK);
        return date;
    }

    public boolean i() {
        return true;
    }

    public boolean r() {
        return false;
    }

    public G p() {
        return this.e;
    }

    public boolean K(w temporal) {
        if (!temporal.h(j.DAY_OF_WEEK)) {
            return false;
        }
        E e2 = this.d;
        if (e2 == k.WEEKS) {
            return true;
        }
        if (e2 == k.MONTHS) {
            return temporal.h(j.DAY_OF_MONTH);
        }
        if (e2 == k.YEARS) {
            return temporal.h(j.DAY_OF_YEAR);
        }
        if (e2 == I.h) {
            return temporal.h(j.DAY_OF_YEAR);
        }
        if (e2 == k.FOREVER) {
            return temporal.h(j.YEAR);
        }
        return false;
    }

    public G M(w temporal) {
        E e2 = this.d;
        if (e2 == k.WEEKS) {
            return this.e;
        }
        if (e2 == k.MONTHS) {
            return o(temporal, j.DAY_OF_MONTH);
        }
        if (e2 == k.YEARS) {
            return o(temporal, j.DAY_OF_YEAR);
        }
        if (e2 == I.h) {
            return q(temporal);
        }
        if (e2 == k.FOREVER) {
            return j.YEAR.p();
        }
        throw new IllegalStateException("unreachable, rangeUnit: " + this.d + ", this: " + this);
    }

    private G o(w temporal, B field) {
        int offset = w(temporal.i(field), c(temporal));
        G fieldRange = temporal.p(field);
        return G.j((long) a(offset, (int) fieldRange.e()), (long) a(offset, (int) fieldRange.d()));
    }

    private G q(w temporal) {
        if (!temporal.h(j.DAY_OF_YEAR)) {
            return h;
        }
        int dow = c(temporal);
        int doy = temporal.i(j.DAY_OF_YEAR);
        int offset = w(doy, dow);
        int week = a(offset, doy);
        if (week == 0) {
            return q(p.e(temporal).o(temporal).I((long) (doy + 7), k.DAYS));
        }
        int yearLen = (int) temporal.p(j.DAY_OF_YEAR).d();
        int newYearWeek = a(offset, this.b.f() + yearLen);
        if (week >= newYearWeek) {
            return q(p.e(temporal).o(temporal).g((long) ((yearLen - doy) + 1 + 7), k.DAYS));
        }
        return G.j(1, (long) (newYearWeek - 1));
    }

    public String toString() {
        return this.a + "[" + this.b.toString() + "]";
    }
}
