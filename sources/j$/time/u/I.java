package j$.time.u;

import j$.CLASSNAMEp;
import j$.time.g;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

public final class I implements Serializable {
    private static final ConcurrentMap g = new ConcurrentHashMap(4, 0.75f, 2);
    public static final E h = s.d;
    private final g a;
    private final int b;
    /* access modifiers changed from: private */
    public final transient B c = H.h(this);
    private final transient B d = H.l(this);
    /* access modifiers changed from: private */
    public final transient B e;
    /* access modifiers changed from: private */
    public final transient B f;

    static {
        new I(g.MONDAY, 4);
        g(g.SUNDAY, 1);
    }

    public static I h(Locale locale) {
        CLASSNAMEp.a(locale, "locale");
        Calendar cal = Calendar.getInstance(new Locale(locale.getLanguage(), locale.getCountry()));
        return g(g.SUNDAY.K((long) (cal.getFirstDayOfWeek() - 1)), cal.getMinimalDaysInFirstWeek());
    }

    public static I g(g firstDayOfWeek, int minimalDaysInFirstWeek) {
        String key = firstDayOfWeek.toString() + minimalDaysInFirstWeek;
        I rules = (I) g.get(key);
        if (rules != null) {
            return rules;
        }
        g.putIfAbsent(key, new I(firstDayOfWeek, minimalDaysInFirstWeek));
        return (I) g.get(key);
    }

    private I(g firstDayOfWeek, int minimalDaysInFirstWeek) {
        H.n(this);
        this.e = H.m(this);
        this.f = H.k(this);
        CLASSNAMEp.a(firstDayOfWeek, "firstDayOfWeek");
        if (minimalDaysInFirstWeek < 1 || minimalDaysInFirstWeek > 7) {
            throw new IllegalArgumentException("Minimal number of days is invalid");
        }
        this.a = firstDayOfWeek;
        this.b = minimalDaysInFirstWeek;
    }

    public g e() {
        return this.a;
    }

    public int f() {
        return this.b;
    }

    public B d() {
        return this.c;
    }

    public B j() {
        return this.d;
    }

    public B k() {
        return this.e;
    }

    public B i() {
        return this.f;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof I) || hashCode() != object.hashCode()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.a.ordinal() * 7) + this.b;
    }

    public String toString() {
        return "WeekFields[" + this.a + ',' + this.b + ']';
    }
}
