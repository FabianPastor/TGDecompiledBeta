package j$.time;

import j$.time.t.p;
import j$.time.t.t;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.v;
import j$.time.u.w;
import j$.time.u.x;

public enum j implements w, x {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;
    
    private static final j[] m = null;

    static {
        m = values();
    }

    public static j L(int month) {
        if (month >= 1 && month <= 12) {
            return m[month - 1];
        }
        throw new f("Invalid value for MonthOfYear: " + month);
    }

    public int getValue() {
        return ordinal() + 1;
    }

    public boolean h(B field) {
        if (field instanceof j$.time.u.j) {
            if (field == j$.time.u.j.MONTH_OF_YEAR) {
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
        if (field == j$.time.u.j.MONTH_OF_YEAR) {
            return field.p();
        }
        return v.c(this, field);
    }

    public int i(B field) {
        if (field == j$.time.u.j.MONTH_OF_YEAR) {
            return getValue();
        }
        return v.a(this, field);
    }

    public long f(B field) {
        if (field == j$.time.u.j.MONTH_OF_YEAR) {
            return (long) getValue();
        }
        if (!(field instanceof j$.time.u.j)) {
            return field.A(this);
        }
        throw new F("Unsupported field: " + field);
    }

    public j M(long months) {
        return m[(ordinal() + (((int) (months % 12)) + 12)) % 12];
    }

    public int K(boolean leapYear) {
        int ordinal = ordinal();
        if (ordinal == 1) {
            return leapYear ? 29 : 28;
        }
        if (ordinal == 3 || ordinal == 5 || ordinal == 8 || ordinal == 10) {
            return 30;
        }
        return 31;
    }

    public int A(boolean leapYear) {
        int leap = leapYear;
        switch (ordinal()) {
            case 0:
                return 1;
            case 1:
                return 32;
            case 2:
                return leap + 60;
            case 3:
                return ((int) leap) + true;
            case 4:
                return leap + 121;
            case 5:
                return leap + 152;
            case 6:
                return leap + 182;
            case 7:
                return leap + 213;
            case 8:
                return leap + 244;
            case 9:
                return leap + 274;
            case 10:
                return leap + 305;
            default:
                return leap + 335;
        }
    }

    public Object r(D d) {
        if (d == C.a()) {
            return t.a;
        }
        if (d == C.l()) {
            return k.MONTHS;
        }
        return v.b(this, d);
    }

    public u x(u temporal) {
        if (p.e(temporal).equals(t.a)) {
            return temporal.c(j$.time.u.j.MONTH_OF_YEAR, (long) getValue());
        }
        throw new f("Adjustment only supported on ISO date-time");
    }
}
