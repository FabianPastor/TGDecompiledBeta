package j$.time;

import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.v;
import j$.time.u.w;
import j$.time.u.x;

public enum g implements w, x {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;
    
    private static final g[] h = null;

    static {
        h = values();
    }

    public static g A(int dayOfWeek) {
        if (dayOfWeek >= 1 && dayOfWeek <= 7) {
            return h[dayOfWeek - 1];
        }
        throw new f("Invalid value for DayOfWeek: " + dayOfWeek);
    }

    public int getValue() {
        return ordinal() + 1;
    }

    public boolean h(B field) {
        if (field instanceof j) {
            if (field == j.DAY_OF_WEEK) {
                return true;
            }
            return false;
        } else if (field == null || !field.K(this)) {
            return false;
        } else {
            return true;
        }
    }

    public G p(B field) {
        if (field == j.DAY_OF_WEEK) {
            return field.p();
        }
        return v.c(this, field);
    }

    public int i(B field) {
        if (field == j.DAY_OF_WEEK) {
            return getValue();
        }
        return v.a(this, field);
    }

    public long f(B field) {
        if (field == j.DAY_OF_WEEK) {
            return (long) getValue();
        }
        if (!(field instanceof j)) {
            return field.A(this);
        }
        throw new F("Unsupported field: " + field);
    }

    public g K(long days) {
        return h[(ordinal() + (((int) (days % 7)) + 7)) % 7];
    }

    public Object r(D d) {
        if (d == C.l()) {
            return k.DAYS;
        }
        return v.b(this, d);
    }

    public u x(u temporal) {
        return temporal.c(j.DAY_OF_WEEK, (long) getValue());
    }
}
