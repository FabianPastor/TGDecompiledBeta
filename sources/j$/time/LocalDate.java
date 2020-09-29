package j$.time;

import j$.CLASSNAMEe;
import j$.CLASSNAMEf;
import j$.CLASSNAMEj;
import j$.CLASSNAMEk;
import j$.CLASSNAMEp;
import j$.time.format.DateTimeFormatter;
import j$.time.t.e;
import j$.time.t.f;
import j$.time.t.s;
import j$.time.t.t;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.E;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.v;
import j$.time.u.w;
import j$.time.u.x;
import j$.time.u.z;
import java.io.Serializable;

public final class LocalDate implements u, x, f, Serializable {
    public static final LocalDate d = a0(-NUM, 1, 1);
    public static final LocalDate e = a0(NUM, 12, 31);
    private final int a;
    private final short b;
    private final short c;

    public static LocalDate Z(e clock) {
        CLASSNAMEp.a(clock, "clock");
        i now = clock.b();
        return b0(CLASSNAMEf.a(now.M() + ((long) clock.a().A().d(now).U()), (long) 86400));
    }

    public static LocalDate a0(int year, int month, int dayOfMonth) {
        j.YEAR.P((long) year);
        j.MONTH_OF_YEAR.P((long) month);
        j.DAY_OF_MONTH.P((long) dayOfMonth);
        return L(year, month, dayOfMonth);
    }

    public static LocalDate c0(int year, int dayOfYear) {
        j.YEAR.P((long) year);
        j.DAY_OF_YEAR.P((long) dayOfYear);
        boolean leap = t.a.b0((long) year);
        if (dayOfYear != 366 || leap) {
            j moy = j.L(((dayOfYear - 1) / 31) + 1);
            if (dayOfYear > (moy.A(leap) + moy.K(leap)) - 1) {
                moy = moy.M(1);
            }
            return new LocalDate(year, moy.getValue(), (dayOfYear - moy.A(leap)) + 1);
        }
        throw new f("Invalid date 'DayOfYear 366' as '" + year + "' is not a leap year");
    }

    public static LocalDate b0(long epochDay) {
        long zeroDay = (epochDay + 719528) - 60;
        long adjust = 0;
        if (zeroDay < 0) {
            long adjustCycles = ((zeroDay + 1) / 146097) - 1;
            adjust = adjustCycles * 400;
            zeroDay += (-adjustCycles) * 146097;
        }
        long yearEst = ((zeroDay * 400) + 591) / 146097;
        long doyEst = zeroDay - ((((yearEst * 365) + (yearEst / 4)) - (yearEst / 100)) + (yearEst / 400));
        if (doyEst < 0) {
            yearEst--;
            doyEst = zeroDay - ((((365 * yearEst) + (yearEst / 4)) - (yearEst / 100)) + (yearEst / 400));
        }
        int marchDoy0 = (int) doyEst;
        int marchMonth0 = ((marchDoy0 * 5) + 2) / 153;
        return new LocalDate(j.YEAR.O(yearEst + adjust + ((long) (marchMonth0 / 10))), ((marchMonth0 + 2) % 12) + 1, (marchDoy0 - (((marchMonth0 * 306) + 5) / 10)) + 1);
    }

    public static LocalDate M(w temporal) {
        CLASSNAMEp.a(temporal, "temporal");
        LocalDate date = (LocalDate) temporal.r(C.i());
        if (date != null) {
            return date;
        }
        throw new f("Unable to obtain LocalDate from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
    }

    public static LocalDate parse(CharSequence text, DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        return (LocalDate) formatter.j(text, b.a);
    }

    private static LocalDate L(int year, int month, int dayOfMonth) {
        int i = 28;
        if (dayOfMonth > 28) {
            int dom = 31;
            if (month == 2) {
                if (t.a.b0((long) year)) {
                    i = 29;
                }
                dom = i;
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                dom = 30;
            }
            if (dayOfMonth > dom) {
                if (dayOfMonth == 29) {
                    throw new f("Invalid date 'February 29' as '" + year + "' is not a leap year");
                }
                throw new f("Invalid date '" + j.L(month).name() + " " + dayOfMonth + "'");
            }
        }
        return new LocalDate(year, month, dayOfMonth);
    }

    private static LocalDate j0(int year, int month, int day) {
        if (month == 2) {
            day = Math.min(day, t.a.b0((long) year) ? 29 : 28);
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = Math.min(day, 30);
        }
        return new LocalDate(year, month, day);
    }

    private LocalDate(int year, int month, int dayOfMonth) {
        this.a = year;
        this.b = (short) month;
        this.c = (short) dayOfMonth;
    }

    public boolean h(B field) {
        return e.d(this, field);
    }

    public G p(B field) {
        if (!(field instanceof j)) {
            return field.M(this);
        }
        j f = (j) field;
        if (f.i()) {
            int ordinal = f.ordinal();
            if (ordinal == 18) {
                return G.j(1, (long) W());
            }
            if (ordinal == 19) {
                return G.j(1, (long) lengthOfYear());
            }
            if (ordinal == 21) {
                return G.j(1, (S() != j.FEBRUARY || V()) ? 5 : 4);
            } else if (ordinal != 25) {
                return field.p();
            } else {
                return G.j(1, U() <= 0 ? NUM : NUM);
            }
        } else {
            throw new F("Unsupported field: " + field);
        }
    }

    public int i(B field) {
        if (field instanceof j) {
            return O(field);
        }
        return v.a(this, field);
    }

    public long f(B field) {
        if (!(field instanceof j)) {
            return field.A(this);
        }
        if (field == j.EPOCH_DAY) {
            return toEpochDay();
        }
        if (field == j.PROLEPTIC_MONTH) {
            return T();
        }
        return (long) O(field);
    }

    private int O(B field) {
        switch (((j) field).ordinal()) {
            case 15:
                return Q().getValue();
            case 16:
                return ((this.c - 1) % 7) + 1;
            case 17:
                return ((R() - 1) % 7) + 1;
            case 18:
                return this.c;
            case 19:
                return R();
            case 20:
                throw new F("Invalid field 'EpochDay' for get() method, use getLong() instead");
            case 21:
                return ((this.c - 1) / 7) + 1;
            case 22:
                return ((R() - 1) / 7) + 1;
            case 23:
                return this.b;
            case 24:
                throw new F("Invalid field 'ProlepticMonth' for get() method, use getLong() instead");
            case 25:
                int i = this.a;
                return i >= 1 ? i : 1 - i;
            case 26:
                return this.a;
            case 27:
                if (this.a >= 1) {
                    return 1;
                }
                return 0;
            default:
                throw new F("Unsupported field: " + field);
        }
    }

    private long T() {
        return ((((long) this.a) * 12) + ((long) this.b)) - 1;
    }

    /* renamed from: P */
    public t b() {
        return t.a;
    }

    public s y() {
        return e.c(this);
    }

    public int U() {
        return this.a;
    }

    public j S() {
        return j.L(this.b);
    }

    public int R() {
        return (S().A(V()) + this.c) - 1;
    }

    public g Q() {
        return g.A(CLASSNAMEj.a(toEpochDay() + 3, 7) + 1);
    }

    public boolean V() {
        return t.a.b0((long) this.a);
    }

    public int W() {
        short s = this.b;
        if (s == 2) {
            return V() ? 29 : 28;
        }
        if (s == 4 || s == 6 || s == 9 || s == 11) {
            return 30;
        }
        return 31;
    }

    public int lengthOfYear() {
        return V() ? 366 : 365;
    }

    /* renamed from: k0 */
    public LocalDate a(x adjuster) {
        if (adjuster instanceof LocalDate) {
            return (LocalDate) adjuster;
        }
        return (LocalDate) adjuster.x(this);
    }

    /* renamed from: l0 */
    public LocalDate c(B field, long newValue) {
        if (!(field instanceof j)) {
            return (LocalDate) field.L(this, newValue);
        }
        j f = (j) field;
        f.P(newValue);
        switch (f.ordinal()) {
            case 15:
                return f0(newValue - ((long) Q().getValue()));
            case 16:
                return f0(newValue - f(j.ALIGNED_DAY_OF_WEEK_IN_MONTH));
            case 17:
                return f0(newValue - f(j.ALIGNED_DAY_OF_WEEK_IN_YEAR));
            case 18:
                return m0((int) newValue);
            case 19:
                return n0((int) newValue);
            case 20:
                return b0(newValue);
            case 21:
                return h0(newValue - f(j.ALIGNED_WEEK_OF_MONTH));
            case 22:
                return h0(newValue - f(j.ALIGNED_WEEK_OF_YEAR));
            case 23:
                return o0((int) newValue);
            case 24:
                return g0(newValue - T());
            case 25:
                return p0((int) (this.a >= 1 ? newValue : 1 - newValue));
            case 26:
                return p0((int) newValue);
            case 27:
                return f(j.ERA) == newValue ? this : p0(1 - this.a);
            default:
                throw new F("Unsupported field: " + field);
        }
    }

    public LocalDate p0(int year) {
        if (this.a == year) {
            return this;
        }
        j.YEAR.P((long) year);
        return j0(year, this.b, this.c);
    }

    public LocalDate o0(int month) {
        if (this.b == month) {
            return this;
        }
        j.MONTH_OF_YEAR.P((long) month);
        return j0(this.a, month, this.c);
    }

    public LocalDate m0(int dayOfMonth) {
        if (this.c == dayOfMonth) {
            return this;
        }
        return a0(this.a, this.b, dayOfMonth);
    }

    public LocalDate n0(int dayOfYear) {
        if (R() == dayOfYear) {
            return this;
        }
        return c0(this.a, dayOfYear);
    }

    /* renamed from: e0 */
    public LocalDate D(z amountToAdd) {
        if (amountToAdd instanceof m) {
            m periodToAdd = (m) amountToAdd;
            return g0(periodToAdd.e()).f0((long) periodToAdd.b());
        }
        CLASSNAMEp.a(amountToAdd, "amountToAdd");
        return (LocalDate) amountToAdd.i(this);
    }

    /* renamed from: d0 */
    public LocalDate g(long amountToAdd, E unit) {
        if (!(unit instanceof k)) {
            return (LocalDate) unit.p(this, amountToAdd);
        }
        switch (((k) unit).ordinal()) {
            case 7:
                return f0(amountToAdd);
            case 8:
                return h0(amountToAdd);
            case 9:
                return g0(amountToAdd);
            case 10:
                return i0(amountToAdd);
            case 11:
                return i0(CLASSNAMEk.a(amountToAdd, (long) 10));
            case 12:
                return i0(CLASSNAMEk.a(amountToAdd, (long) 100));
            case 13:
                return i0(CLASSNAMEk.a(amountToAdd, (long) 1000));
            case 14:
                j jVar = j.ERA;
                return c(jVar, CLASSNAMEe.a(f(jVar), amountToAdd));
            default:
                throw new F("Unsupported unit: " + unit);
        }
    }

    public LocalDate i0(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return j0(j.YEAR.O(((long) this.a) + yearsToAdd), this.b, this.c);
    }

    public LocalDate g0(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long calcMonths = (((long) this.a) * 12) + ((long) (this.b - 1)) + monthsToAdd;
        return j0(j.YEAR.O(CLASSNAMEf.a(calcMonths, (long) 12)), CLASSNAMEj.a(calcMonths, 12) + 1, this.c);
    }

    public LocalDate h0(long weeksToAdd) {
        return f0(CLASSNAMEk.a(weeksToAdd, (long) 7));
    }

    public LocalDate f0(long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        return b0(CLASSNAMEe.a(toEpochDay(), daysToAdd));
    }

    /* renamed from: X */
    public LocalDate I(long amountToSubtract, E unit) {
        return amountToSubtract == Long.MIN_VALUE ? g(Long.MAX_VALUE, unit).g(1, unit) : g(-amountToSubtract, unit);
    }

    public LocalDate Y(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? i0(Long.MAX_VALUE).i0(1) : i0(-yearsToSubtract);
    }

    public Object r(D d2) {
        if (d2 == C.i()) {
            return this;
        }
        return e.e(this, d2);
    }

    public u x(u temporal) {
        return e.a(this, temporal);
    }

    public String format(DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        return formatter.b(this);
    }

    /* renamed from: A */
    public LocalDateTime u(LocalTime time) {
        return LocalDateTime.V(this, time);
    }

    public long toEpochDay() {
        long total;
        long y = (long) this.a;
        long m = (long) this.b;
        long total2 = 0 + (365 * y);
        if (y >= 0) {
            total = total2 + (((3 + y) / 4) - ((99 + y) / 100)) + ((399 + y) / 400);
        } else {
            total = total2 - (((y / -4) - (y / -100)) + (y / -400));
        }
        long total3 = total + (((367 * m) - 362) / 12) + ((long) (this.c - 1));
        if (m > 2) {
            total3--;
            if (!V()) {
                total3--;
            }
        }
        return total3 - 719528;
    }

    /* renamed from: J */
    public int compareTo(f other) {
        if (other instanceof LocalDate) {
            return K((LocalDate) other);
        }
        return e.b(this, other);
    }

    /* access modifiers changed from: package-private */
    public int K(LocalDate otherDate) {
        int cmp = this.a - otherDate.a;
        if (cmp != 0) {
            return cmp;
        }
        int cmp2 = this.b - otherDate.b;
        if (cmp2 == 0) {
            return this.c - otherDate.c;
        }
        return cmp2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalDate) || K((LocalDate) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int yearValue = this.a;
        return (yearValue & -2048) ^ (((yearValue << 11) + (this.b << 6)) + this.c);
    }

    public String toString() {
        int yearValue = this.a;
        int monthValue = this.b;
        int dayValue = this.c;
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(10);
        if (absYear >= 1000) {
            if (yearValue > 9999) {
                buf.append('+');
            }
            buf.append(yearValue);
        } else if (yearValue < 0) {
            buf.append(yearValue - 10000);
            buf.deleteCharAt(1);
        } else {
            buf.append(yearValue + 10000);
            buf.deleteCharAt(0);
        }
        String str = "-0";
        buf.append(monthValue < 10 ? str : "-");
        buf.append(monthValue);
        if (dayValue >= 10) {
            str = "-";
        }
        buf.append(str);
        buf.append(dayValue);
        return buf.toString();
    }
}
