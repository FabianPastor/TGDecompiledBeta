package j$.time.t;

import j$.CLASSNAMEd;
import j$.CLASSNAMEm;
import j$.time.LocalDate;
import j$.time.e;
import j$.time.f;
import j$.time.format.I;
import j$.time.g;
import j$.time.i;
import j$.time.o;
import j$.time.u.B;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.w;
import j$.time.u.y;
import j$.util.concurrent.ConcurrentHashMap;
import java.time.chrono.Era;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class d implements q {
    public /* synthetic */ f G(e eVar) {
        return p.b(this, eVar);
    }

    public /* synthetic */ m H(i iVar, o oVar) {
        return p.d(this, iVar, oVar);
    }

    public /* synthetic */ f p() {
        return p.a(this);
    }

    public /* synthetic */ i v(w wVar) {
        return p.c(this, wVar);
    }

    static {
        b bVar = b.a;
        c cVar = c.a;
        a aVar = a.a;
        new ConcurrentHashMap();
        new ConcurrentHashMap();
        new Locale("ja", "JP", "JP");
    }

    static /* synthetic */ int x(f date1, f date2) {
        return (date1.toEpochDay() > date2.toEpochDay() ? 1 : (date1.toEpochDay() == date2.toEpochDay() ? 0 : -1));
    }

    static /* synthetic */ int A(i dateTime1, i dateTime2) {
        int cmp = (dateTime1.e().toEpochDay() > dateTime2.e().toEpochDay() ? 1 : (dateTime1.e().toEpochDay() == dateTime2.e().toEpochDay() ? 0 : -1));
        if (cmp == 0) {
            return (dateTime1.d().Y() > dateTime2.d().Y() ? 1 : (dateTime1.d().Y() == dateTime2.d().Y() ? 0 : -1));
        }
        return cmp;
    }

    static /* synthetic */ int r(m dateTime1, m dateTime2) {
        int cmp = (dateTime1.toEpochSecond() > dateTime2.toEpochSecond() ? 1 : (dateTime1.toEpochSecond() == dateTime2.toEpochSecond() ? 0 : -1));
        if (cmp == 0) {
            return (((long) dateTime1.d().O()) > ((long) dateTime2.d().O()) ? 1 : (((long) dateTime1.d().O()) == ((long) dateTime2.d().O()) ? 0 : -1));
        }
        return cmp;
    }

    protected d() {
    }

    public f E(Map fieldValues, I resolverStyle) {
        if (fieldValues.containsKey(j.EPOCH_DAY)) {
            return m(((Long) fieldValues.remove(j.EPOCH_DAY)).longValue());
        }
        L(fieldValues, resolverStyle);
        T(fieldValues, resolverStyle);
        if (0 != 0 || !fieldValues.containsKey(j.YEAR)) {
            return null;
        }
        if (fieldValues.containsKey(j.MONTH_OF_YEAR)) {
            if (fieldValues.containsKey(j.DAY_OF_MONTH)) {
                return S(fieldValues, resolverStyle);
            }
            if (fieldValues.containsKey(j.ALIGNED_WEEK_OF_MONTH)) {
                if (fieldValues.containsKey(j.ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                    return Q(fieldValues, resolverStyle);
                }
                if (fieldValues.containsKey(j.DAY_OF_WEEK)) {
                    return R(fieldValues, resolverStyle);
                }
            }
        }
        if (fieldValues.containsKey(j.DAY_OF_YEAR)) {
            return P(fieldValues, resolverStyle);
        }
        if (!fieldValues.containsKey(j.ALIGNED_WEEK_OF_YEAR)) {
            return null;
        }
        if (fieldValues.containsKey(j.ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
            return M(fieldValues, resolverStyle);
        }
        if (fieldValues.containsKey(j.DAY_OF_WEEK)) {
            return O(fieldValues, resolverStyle);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void L(Map fieldValues, I resolverStyle) {
        Long pMonth = (Long) fieldValues.remove(j.PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != I.LENIENT) {
                j.PROLEPTIC_MONTH.P(pMonth.longValue());
            }
            f chronoDate = ((LocalDate) ((LocalDate) p()).c((B) j.DAY_OF_MONTH, 1)).c((B) j.PROLEPTIC_MONTH, pMonth.longValue());
            j jVar = j.MONTH_OF_YEAR;
            i(fieldValues, jVar, (long) ((LocalDate) chronoDate).i(jVar));
            j jVar2 = j.YEAR;
            i(fieldValues, jVar2, (long) ((LocalDate) chronoDate).i(jVar2));
        }
    }

    /* access modifiers changed from: package-private */
    public f T(Map fieldValues, I resolverStyle) {
        int yoe;
        Long yoeLong = (Long) fieldValues.remove(j.YEAR_OF_ERA);
        if (yoeLong != null) {
            Long eraLong = (Long) fieldValues.remove(j.ERA);
            if (resolverStyle != I.LENIENT) {
                yoe = F(j.YEAR_OF_ERA).a(yoeLong.longValue(), j.YEAR_OF_ERA);
            } else {
                yoe = CLASSNAMEd.a(yoeLong.longValue());
            }
            if (eraLong != null) {
                i(fieldValues, j.YEAR, (long) k(N(F(j.ERA).a(eraLong.longValue(), j.ERA)), yoe));
                return null;
            } else if (fieldValues.containsKey(j.YEAR)) {
                i(fieldValues, j.YEAR, (long) k(t(F(j.YEAR).a(((Long) fieldValues.get(j.YEAR)).longValue(), j.YEAR), 1).y(), yoe));
                return null;
            } else if (resolverStyle == I.STRICT) {
                fieldValues.put(j.YEAR_OF_ERA, yoeLong);
                return null;
            } else {
                List<Era> eras = eras();
                if (eras.isEmpty()) {
                    i(fieldValues, j.YEAR, (long) yoe);
                    return null;
                }
                i(fieldValues, j.YEAR, (long) k((s) eras.get(eras.size() - 1), yoe));
                return null;
            }
        } else if (!fieldValues.containsKey(j.ERA)) {
            return null;
        } else {
            F(j.ERA).b(((Long) fieldValues.get(j.ERA)).longValue(), j.ERA);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public f S(Map fieldValues, I resolverStyle) {
        int y = F(j.YEAR).a(((Long) fieldValues.remove(j.YEAR)).longValue(), j.YEAR);
        if (resolverStyle == I.LENIENT) {
            long months = CLASSNAMEm.a(((Long) fieldValues.remove(j.MONTH_OF_YEAR)).longValue(), 1);
            return C(y, 1, 1).g(months, k.MONTHS).g(CLASSNAMEm.a(((Long) fieldValues.remove(j.DAY_OF_MONTH)).longValue(), 1), k.DAYS);
        }
        int moy = F(j.MONTH_OF_YEAR).a(((Long) fieldValues.remove(j.MONTH_OF_YEAR)).longValue(), j.MONTH_OF_YEAR);
        int dom = F(j.DAY_OF_MONTH).a(((Long) fieldValues.remove(j.DAY_OF_MONTH)).longValue(), j.DAY_OF_MONTH);
        if (resolverStyle != I.SMART) {
            return C(y, moy, dom);
        }
        try {
            return C(y, moy, dom);
        } catch (f e) {
            return C(y, moy, 1).a(y.c());
        }
    }

    /* access modifiers changed from: package-private */
    public f P(Map fieldValues, I resolverStyle) {
        int y = F(j.YEAR).a(((Long) fieldValues.remove(j.YEAR)).longValue(), j.YEAR);
        if (resolverStyle != I.LENIENT) {
            return t(y, F(j.DAY_OF_YEAR).a(((Long) fieldValues.remove(j.DAY_OF_YEAR)).longValue(), j.DAY_OF_YEAR));
        }
        return t(y, 1).g(CLASSNAMEm.a(((Long) fieldValues.remove(j.DAY_OF_YEAR)).longValue(), 1), k.DAYS);
    }

    /* access modifiers changed from: package-private */
    public f Q(Map fieldValues, I resolverStyle) {
        int y = F(j.YEAR).a(((Long) fieldValues.remove(j.YEAR)).longValue(), j.YEAR);
        if (resolverStyle == I.LENIENT) {
            long months = CLASSNAMEm.a(((Long) fieldValues.remove(j.MONTH_OF_YEAR)).longValue(), 1);
            long weeks = CLASSNAMEm.a(((Long) fieldValues.remove(j.ALIGNED_WEEK_OF_MONTH)).longValue(), 1);
            return C(y, 1, 1).g(months, k.MONTHS).g(weeks, k.WEEKS).g(CLASSNAMEm.a(((Long) fieldValues.remove(j.ALIGNED_DAY_OF_WEEK_IN_MONTH)).longValue(), 1), k.DAYS);
        }
        int moy = F(j.MONTH_OF_YEAR).a(((Long) fieldValues.remove(j.MONTH_OF_YEAR)).longValue(), j.MONTH_OF_YEAR);
        f date = C(y, moy, 1).g((long) (((F(j.ALIGNED_WEEK_OF_MONTH).a(((Long) fieldValues.remove(j.ALIGNED_WEEK_OF_MONTH)).longValue(), j.ALIGNED_WEEK_OF_MONTH) - 1) * 7) + (F(j.ALIGNED_DAY_OF_WEEK_IN_MONTH).a(((Long) fieldValues.remove(j.ALIGNED_DAY_OF_WEEK_IN_MONTH)).longValue(), j.ALIGNED_DAY_OF_WEEK_IN_MONTH) - 1)), k.DAYS);
        if (resolverStyle != I.STRICT || date.i(j.MONTH_OF_YEAR) == moy) {
            return date;
        }
        throw new f("Strict mode rejected resolved date as it is in a different month");
    }

    /* access modifiers changed from: package-private */
    public f R(Map fieldValues, I resolverStyle) {
        Map map = fieldValues;
        I i = resolverStyle;
        int y = F(j.YEAR).a(((Long) map.remove(j.YEAR)).longValue(), j.YEAR);
        if (i == I.LENIENT) {
            return K(C(y, 1, 1), CLASSNAMEm.a(((Long) map.remove(j.MONTH_OF_YEAR)).longValue(), 1), CLASSNAMEm.a(((Long) map.remove(j.ALIGNED_WEEK_OF_MONTH)).longValue(), 1), CLASSNAMEm.a(((Long) map.remove(j.DAY_OF_WEEK)).longValue(), 1));
        }
        int moy = F(j.MONTH_OF_YEAR).a(((Long) map.remove(j.MONTH_OF_YEAR)).longValue(), j.MONTH_OF_YEAR);
        f date = C(y, moy, 1).g((long) ((F(j.ALIGNED_WEEK_OF_MONTH).a(((Long) map.remove(j.ALIGNED_WEEK_OF_MONTH)).longValue(), j.ALIGNED_WEEK_OF_MONTH) - 1) * 7), k.DAYS).a(y.d(g.A(F(j.DAY_OF_WEEK).a(((Long) map.remove(j.DAY_OF_WEEK)).longValue(), j.DAY_OF_WEEK))));
        if (i != I.STRICT || date.i(j.MONTH_OF_YEAR) == moy) {
            return date;
        }
        throw new f("Strict mode rejected resolved date as it is in a different month");
    }

    /* access modifiers changed from: package-private */
    public f M(Map fieldValues, I resolverStyle) {
        int y = F(j.YEAR).a(((Long) fieldValues.remove(j.YEAR)).longValue(), j.YEAR);
        if (resolverStyle == I.LENIENT) {
            long weeks = CLASSNAMEm.a(((Long) fieldValues.remove(j.ALIGNED_WEEK_OF_YEAR)).longValue(), 1);
            return t(y, 1).g(weeks, k.WEEKS).g(CLASSNAMEm.a(((Long) fieldValues.remove(j.ALIGNED_DAY_OF_WEEK_IN_YEAR)).longValue(), 1), k.DAYS);
        }
        f date = t(y, 1).g((long) (((F(j.ALIGNED_WEEK_OF_YEAR).a(((Long) fieldValues.remove(j.ALIGNED_WEEK_OF_YEAR)).longValue(), j.ALIGNED_WEEK_OF_YEAR) - 1) * 7) + (F(j.ALIGNED_DAY_OF_WEEK_IN_YEAR).a(((Long) fieldValues.remove(j.ALIGNED_DAY_OF_WEEK_IN_YEAR)).longValue(), j.ALIGNED_DAY_OF_WEEK_IN_YEAR) - 1)), k.DAYS);
        if (resolverStyle != I.STRICT || date.i(j.YEAR) == y) {
            return date;
        }
        throw new f("Strict mode rejected resolved date as it is in a different year");
    }

    /* access modifiers changed from: package-private */
    public f O(Map fieldValues, I resolverStyle) {
        Map map = fieldValues;
        I i = resolverStyle;
        int y = F(j.YEAR).a(((Long) map.remove(j.YEAR)).longValue(), j.YEAR);
        if (i == I.LENIENT) {
            return K(t(y, 1), 0, CLASSNAMEm.a(((Long) map.remove(j.ALIGNED_WEEK_OF_YEAR)).longValue(), 1), CLASSNAMEm.a(((Long) map.remove(j.DAY_OF_WEEK)).longValue(), 1));
        }
        f date = t(y, 1).g((long) ((F(j.ALIGNED_WEEK_OF_YEAR).a(((Long) map.remove(j.ALIGNED_WEEK_OF_YEAR)).longValue(), j.ALIGNED_WEEK_OF_YEAR) - 1) * 7), k.DAYS).a(y.d(g.A(F(j.DAY_OF_WEEK).a(((Long) map.remove(j.DAY_OF_WEEK)).longValue(), j.DAY_OF_WEEK))));
        if (i != I.STRICT || date.i(j.YEAR) == y) {
            return date;
        }
        throw new f("Strict mode rejected resolved date as it is in a different year");
    }

    /* access modifiers changed from: package-private */
    public f K(f base, long months, long weeks, long dow) {
        f date = base.g(months, k.MONTHS).g(weeks, k.WEEKS);
        if (dow > 7) {
            date = date.g((dow - 1) / 7, k.WEEKS);
            dow = ((dow - 1) % 7) + 1;
        } else if (dow < 1) {
            date = date.g(CLASSNAMEm.a(dow, 7) / 7, k.WEEKS);
            dow = ((6 + dow) % 7) + 1;
        }
        return date.a(y.d(g.A((int) dow)));
    }

    /* access modifiers changed from: package-private */
    public void i(Map fieldValues, j field, long value) {
        Long old = (Long) fieldValues.get(field);
        if (old == null || old.longValue() == value) {
            fieldValues.put(field, Long.valueOf(value));
            return;
        }
        throw new f("Conflict found: " + field + " " + old + " differs from " + field + " " + value);
    }

    /* renamed from: j */
    public int compareTo(q other) {
        return getId().compareTo(other.getId());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d) || compareTo((d) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return getClass().hashCode() ^ getId().hashCode();
    }

    public String toString() {
        return getId();
    }
}
