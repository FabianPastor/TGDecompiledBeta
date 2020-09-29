package j$.time.t;

import j$.CLASSNAMEf;
import j$.CLASSNAMEj;
import j$.CLASSNAMEm;
import j$.CLASSNAMEp;
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.e;
import j$.time.f;
import j$.time.format.I;
import j$.time.i;
import j$.time.n;
import j$.time.o;
import j$.time.s;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.w;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class t extends d implements Serializable {
    public static final t a = new t();

    /* access modifiers changed from: package-private */
    public /* bridge */ /* synthetic */ f T(Map map, I i) {
        f0(map, i);
        return null;
    }

    private t() {
    }

    public String getId() {
        return "ISO";
    }

    /* renamed from: U */
    public LocalDate C(int prolepticYear, int month, int dayOfMonth) {
        return LocalDate.a0(prolepticYear, month, dayOfMonth);
    }

    /* renamed from: Z */
    public LocalDate t(int prolepticYear, int dayOfYear) {
        return LocalDate.c0(prolepticYear, dayOfYear);
    }

    /* renamed from: W */
    public LocalDate m(long epochDay) {
        return LocalDate.b0(epochDay);
    }

    /* renamed from: V */
    public LocalDate o(w temporal) {
        return LocalDate.M(temporal);
    }

    /* renamed from: c0 */
    public LocalDateTime v(w temporal) {
        return LocalDateTime.M(temporal);
    }

    /* renamed from: g0 */
    public s H(i instant, o zone) {
        return s.L(instant, zone);
    }

    /* renamed from: X */
    public LocalDate p() {
        return G(e.d());
    }

    /* renamed from: Y */
    public LocalDate G(e clock) {
        CLASSNAMEp.a(clock, "clock");
        return o(LocalDate.Z(clock));
    }

    public boolean b0(long prolepticYear) {
        return (3 & prolepticYear) == 0 && (prolepticYear % 100 != 0 || prolepticYear % 400 == 0);
    }

    public int k(s era, int yearOfEra) {
        if (era instanceof u) {
            return era == u.CE ? yearOfEra : 1 - yearOfEra;
        }
        throw new ClassCastException("Era must be IsoEra");
    }

    /* renamed from: a0 */
    public u N(int eraValue) {
        return u.A(eraValue);
    }

    public List eras() {
        return Arrays.asList(u.values());
    }

    /* renamed from: d0 */
    public LocalDate E(Map fieldValues, I resolverStyle) {
        return (LocalDate) super.E(fieldValues, resolverStyle);
    }

    /* access modifiers changed from: package-private */
    public void L(Map fieldValues, I resolverStyle) {
        Long pMonth = (Long) fieldValues.remove(j.PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != I.LENIENT) {
                j.PROLEPTIC_MONTH.P(pMonth.longValue());
            }
            i(fieldValues, j.MONTH_OF_YEAR, (long) (CLASSNAMEj.a(pMonth.longValue(), 12) + 1));
            i(fieldValues, j.YEAR, CLASSNAMEf.a(pMonth.longValue(), (long) 12));
        }
    }

    /* access modifiers changed from: package-private */
    public LocalDate f0(Map fieldValues, I resolverStyle) {
        Long yoeLong = (Long) fieldValues.remove(j.YEAR_OF_ERA);
        if (yoeLong != null) {
            if (resolverStyle != I.LENIENT) {
                j.YEAR_OF_ERA.P(yoeLong.longValue());
            }
            Long era = (Long) fieldValues.remove(j.ERA);
            if (era == null) {
                Long year = (Long) fieldValues.get(j.YEAR);
                if (resolverStyle != I.STRICT) {
                    i(fieldValues, j.YEAR, (year == null || year.longValue() > 0) ? yoeLong.longValue() : CLASSNAMEm.a(1, yoeLong.longValue()));
                    return null;
                } else if (year != null) {
                    j jVar = j.YEAR;
                    int i = (year.longValue() > 0 ? 1 : (year.longValue() == 0 ? 0 : -1));
                    long longValue = yoeLong.longValue();
                    if (i <= 0) {
                        longValue = CLASSNAMEm.a(1, longValue);
                    }
                    i(fieldValues, jVar, longValue);
                    return null;
                } else {
                    fieldValues.put(j.YEAR_OF_ERA, yoeLong);
                    return null;
                }
            } else if (era.longValue() == 1) {
                i(fieldValues, j.YEAR, yoeLong.longValue());
                return null;
            } else if (era.longValue() == 0) {
                i(fieldValues, j.YEAR, CLASSNAMEm.a(1, yoeLong.longValue()));
                return null;
            } else {
                throw new f("Invalid value for era: " + era);
            }
        } else if (!fieldValues.containsKey(j.ERA)) {
            return null;
        } else {
            j jVar2 = j.ERA;
            jVar2.P(((Long) fieldValues.get(jVar2)).longValue());
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: e0 */
    public LocalDate S(Map fieldValues, I resolverStyle) {
        j jVar = j.YEAR;
        int y = jVar.O(((Long) fieldValues.remove(jVar)).longValue());
        if (resolverStyle == I.LENIENT) {
            long months = CLASSNAMEm.a(((Long) fieldValues.remove(j.MONTH_OF_YEAR)).longValue(), 1);
            return LocalDate.a0(y, 1, 1).g0(months).f0(CLASSNAMEm.a(((Long) fieldValues.remove(j.DAY_OF_MONTH)).longValue(), 1));
        }
        j jVar2 = j.MONTH_OF_YEAR;
        int moy = jVar2.O(((Long) fieldValues.remove(jVar2)).longValue());
        j jVar3 = j.DAY_OF_MONTH;
        int dom = jVar3.O(((Long) fieldValues.remove(jVar3)).longValue());
        if (resolverStyle == I.SMART) {
            if (moy == 4 || moy == 6 || moy == 9 || moy == 11) {
                dom = Math.min(dom, 30);
            } else if (moy == 2) {
                dom = Math.min(dom, j$.time.j.FEBRUARY.K(n.A((long) y)));
            }
        }
        return LocalDate.a0(y, moy, dom);
    }

    public G F(j field) {
        return field.p();
    }
}
