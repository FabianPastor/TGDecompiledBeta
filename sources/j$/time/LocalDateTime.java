package j$.time;

import j$.CLASSNAMEf;
import j$.CLASSNAMEi;
import j$.CLASSNAMEj;
import j$.CLASSNAMEp;
import j$.time.format.DateTimeFormatter;
import j$.time.t.h;
import j$.time.t.i;
import j$.time.t.q;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.E;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.v;
import j$.time.u.w;
import j$.time.u.x;
import java.io.Serializable;

public final class LocalDateTime implements u, x, i, Serializable {
    public static final LocalDateTime c = V(LocalDate.d, LocalTime.e);
    public static final LocalDateTime d = V(LocalDate.e, LocalTime.f);
    private final LocalDate a;
    private final LocalTime b;

    public /* synthetic */ q b() {
        return h.d(this);
    }

    public /* synthetic */ i e0(p pVar) {
        return h.i(this, pVar);
    }

    public /* synthetic */ long w(p pVar) {
        return h.h(this, pVar);
    }

    public static LocalDateTime T(int year, int month, int dayOfMonth, int hour, int minute) {
        return new LocalDateTime(LocalDate.a0(year, month, dayOfMonth), LocalTime.Q(hour, minute));
    }

    public static LocalDateTime U(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return new LocalDateTime(LocalDate.a0(year, month, dayOfMonth), LocalTime.R(hour, minute, second, nanoOfSecond));
    }

    public static LocalDateTime V(LocalDate date, LocalTime time) {
        CLASSNAMEp.a(date, "date");
        CLASSNAMEp.a(time, "time");
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime W(long epochSecond, int nanoOfSecond, p offset) {
        CLASSNAMEp.a(offset, "offset");
        j.NANO_OF_SECOND.P((long) nanoOfSecond);
        long localSecond = ((long) offset.U()) + epochSecond;
        return new LocalDateTime(LocalDate.b0(CLASSNAMEf.a(localSecond, (long) 86400)), LocalTime.S((((long) CLASSNAMEj.a(localSecond, 86400)) * NUM) + ((long) nanoOfSecond)));
    }

    public static LocalDateTime M(w temporal) {
        if (temporal instanceof LocalDateTime) {
            return (LocalDateTime) temporal;
        }
        if (temporal instanceof s) {
            return ((s) temporal).B();
        }
        if (temporal instanceof l) {
            return ((l) temporal).R();
        }
        try {
            return new LocalDateTime(LocalDate.M(temporal), LocalTime.L(temporal));
        } catch (f ex) {
            throw new f("Unable to obtain LocalDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        return (LocalDateTime) formatter.j(text, c.a);
    }

    private LocalDateTime(LocalDate date, LocalTime time) {
        this.a = date;
        this.b = time;
    }

    private LocalDateTime g0(LocalDate newDate, LocalTime newTime) {
        if (this.a == newDate && this.b == newTime) {
            return this;
        }
        return new LocalDateTime(newDate, newTime);
    }

    public boolean h(B field) {
        if (field instanceof j) {
            j f = (j) field;
            if (f.i() || f.r()) {
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
        if (field instanceof j) {
            return ((j) field).r() ? this.b.p(field) : this.a.p(field);
        }
        return field.M(this);
    }

    public int i(B field) {
        if (field instanceof j) {
            return ((j) field).r() ? this.b.i(field) : this.a.i(field);
        }
        return v.a(this, field);
    }

    public long f(B field) {
        if (field instanceof j) {
            return ((j) field).r() ? this.b.f(field) : this.a.f(field);
        }
        return field.A(this);
    }

    /* renamed from: f0 */
    public LocalDate e() {
        return this.a;
    }

    public int Q() {
        return this.a.U();
    }

    public LocalTime d() {
        return this.b;
    }

    public int P() {
        return this.b.P();
    }

    public int O() {
        return this.b.O();
    }

    /* renamed from: h0 */
    public LocalDateTime a(x adjuster) {
        if (adjuster instanceof LocalDate) {
            return g0((LocalDate) adjuster, this.b);
        }
        if (adjuster instanceof LocalTime) {
            return g0(this.a, (LocalTime) adjuster);
        }
        if (adjuster instanceof LocalDateTime) {
            return (LocalDateTime) adjuster;
        }
        return (LocalDateTime) adjuster.x(this);
    }

    /* renamed from: i0 */
    public LocalDateTime c(B field, long newValue) {
        if (!(field instanceof j)) {
            return (LocalDateTime) field.L(this, newValue);
        }
        if (((j) field).r()) {
            return g0(this.a, this.b.c(field, newValue));
        }
        return g0(this.a.c(field, newValue), this.b);
    }

    /* renamed from: X */
    public LocalDateTime g(long amountToAdd, E unit) {
        if (!(unit instanceof k)) {
            return (LocalDateTime) unit.p(this, amountToAdd);
        }
        switch (((k) unit).ordinal()) {
            case 0:
                return b0(amountToAdd);
            case 1:
                return Y(amountToAdd / 86400000000L).b0((amountToAdd % 86400000000L) * 1000);
            case 2:
                return Y(amountToAdd / 86400000).b0((amountToAdd % 86400000) * 1000000);
            case 3:
                return c0(amountToAdd);
            case 4:
                return a0(amountToAdd);
            case 5:
                return Z(amountToAdd);
            case 6:
                return Y(amountToAdd / 256).Z((amountToAdd % 256) * 12);
            default:
                return g0(this.a.g(amountToAdd, unit), this.b);
        }
    }

    public LocalDateTime Y(long days) {
        return g0(this.a.f0(days), this.b);
    }

    public LocalDateTime Z(long hours) {
        return d0(this.a, hours, 0, 0, 0, 1);
    }

    public LocalDateTime a0(long minutes) {
        return d0(this.a, 0, minutes, 0, 0, 1);
    }

    public LocalDateTime c0(long seconds) {
        return d0(this.a, 0, 0, seconds, 0, 1);
    }

    public LocalDateTime b0(long nanos) {
        return d0(this.a, 0, 0, 0, nanos, 1);
    }

    private LocalDateTime d0(LocalDate newDate, long hours, long minutes, long seconds, long nanos, int sign) {
        LocalDate localDate = newDate;
        int i = sign;
        if ((hours | minutes | seconds | nanos) == 0) {
            return g0(localDate, this.b);
        }
        long curNoD = this.b.Y();
        long totNanos = (((long) i) * ((nanos % 86400000000000L) + ((seconds % 86400) * NUM) + ((minutes % 1440) * 60000000000L) + ((hours % 24) * 3600000000000L))) + curNoD;
        long totDays = (((nanos / 86400000000000L) + (seconds / 86400) + (minutes / 1440) + (hours / 24)) * ((long) i)) + CLASSNAMEf.a(totNanos, 86400000000000L);
        long newNoD = CLASSNAMEi.a(totNanos, 86400000000000L);
        return g0(localDate.f0(totDays), newNoD == curNoD ? this.b : LocalTime.S(newNoD));
    }

    public Object r(D d2) {
        if (d2 == C.i()) {
            return this.a;
        }
        return h.g(this, d2);
    }

    public u x(u temporal) {
        return h.a(this, temporal);
    }

    public String format(DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        return formatter.b(this);
    }

    public l A(p offset) {
        return l.M(this, offset);
    }

    /* renamed from: K */
    public s n(o zone) {
        return s.K(this, zone);
    }

    /* renamed from: z */
    public int compareTo(i iVar) {
        if (iVar instanceof LocalDateTime) {
            return L((LocalDateTime) iVar);
        }
        return h.b(this, iVar);
    }

    private int L(LocalDateTime other) {
        int cmp = this.a.K(other.e());
        if (cmp == 0) {
            return this.b.compareTo(other.d());
        }
        return cmp;
    }

    public boolean R(i iVar) {
        if (iVar instanceof LocalDateTime) {
            return L((LocalDateTime) iVar) > 0;
        }
        return h.e(this, iVar);
    }

    public boolean S(i iVar) {
        if (iVar instanceof LocalDateTime) {
            return L((LocalDateTime) iVar) < 0;
        }
        return h.f(this, iVar);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalDateTime)) {
            return false;
        }
        LocalDateTime other = (LocalDateTime) obj;
        if (!this.a.equals(other.a) || !this.b.equals(other.b)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.a.hashCode() ^ this.b.hashCode();
    }

    public String toString() {
        return this.a.toString() + 'T' + this.b.toString();
    }
}
