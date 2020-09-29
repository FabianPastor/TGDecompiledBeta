package j$.time.t;

import j$.CLASSNAMEf;
import j$.CLASSNAMEi;
import j$.CLASSNAMEp;
import j$.time.LocalTime;
import j$.time.i;
import j$.time.o;
import j$.time.p;
import j$.time.u.B;
import j$.time.u.D;
import j$.time.u.E;
import j$.time.u.G;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.x;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTimeImpl;

final class j implements i, u, x, Serializable {
    private final transient f a;
    private final transient LocalTime b;

    public /* synthetic */ i S(p pVar) {
        return h.i(this, pVar);
    }

    public /* synthetic */ q b() {
        return h.d(this);
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return z((i) obj);
    }

    public /* synthetic */ Object r(D d) {
        return h.g(this, d);
    }

    public /* synthetic */ long w(p pVar) {
        return h.h(this, pVar);
    }

    public /* synthetic */ u x(u uVar) {
        return h.a(this, uVar);
    }

    public /* synthetic */ int z(i iVar) {
        return h.b(this, iVar);
    }

    static j A(q chrono, u temporal) {
        ChronoLocalDateTimeImpl<R> other = (j) temporal;
        if (chrono.equals(other.b())) {
            return other;
        }
        throw new ClassCastException("Chronology mismatch, required: " + chrono.getId() + ", actual: " + other.b().getId());
    }

    private j(f date, LocalTime time) {
        CLASSNAMEp.a(date, "date");
        CLASSNAMEp.a(time, "time");
        this.a = date;
        this.b = time;
    }

    private j T(u newDate, LocalTime newTime) {
        if (this.a == newDate && this.b == newTime) {
            return this;
        }
        return new j(g.A(this.a.b(), newDate), newTime);
    }

    public f e() {
        return this.a;
    }

    public LocalTime d() {
        return this.b;
    }

    public boolean h(B field) {
        if (field instanceof j$.time.u.j) {
            j$.time.u.j f = (j$.time.u.j) field;
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
        if (field instanceof j$.time.u.j) {
            return ((j$.time.u.j) field).r() ? this.b.p(field) : this.a.p(field);
        }
        return field.M(this);
    }

    public int i(B field) {
        if (field instanceof j$.time.u.j) {
            return ((j$.time.u.j) field).r() ? this.b.i(field) : this.a.i(field);
        }
        return p(field).a(f(field), field);
    }

    public long f(B field) {
        if (field instanceof j$.time.u.j) {
            return ((j$.time.u.j) field).r() ? this.b.f(field) : this.a.f(field);
        }
        return field.A(this);
    }

    /* renamed from: U */
    public j a(x adjuster) {
        if (adjuster instanceof f) {
            return T((f) adjuster, this.b);
        }
        if (adjuster instanceof LocalTime) {
            return T(this.a, (LocalTime) adjuster);
        }
        if (adjuster instanceof j) {
            return A(this.a.b(), (j) adjuster);
        }
        return A(this.a.b(), (j) adjuster.x(this));
    }

    /* renamed from: V */
    public j c(B field, long newValue) {
        if (!(field instanceof j$.time.u.j)) {
            return A(this.a.b(), field.L(this, newValue));
        }
        if (((j$.time.u.j) field).r()) {
            return T(this.a, this.b.c(field, newValue));
        }
        return T(this.a.c(field, newValue), this.b);
    }

    /* renamed from: K */
    public j g(long amountToAdd, E unit) {
        if (!(unit instanceof k)) {
            return A(this.a.b(), unit.p(this, amountToAdd));
        }
        switch (((k) unit).ordinal()) {
            case 0:
                return P(amountToAdd);
            case 1:
                return L(amountToAdd / 86400000000L).P((amountToAdd % 86400000000L) * 1000);
            case 2:
                return L(amountToAdd / 86400000).P((amountToAdd % 86400000) * 1000000);
            case 3:
                return Q(amountToAdd);
            case 4:
                return O(amountToAdd);
            case 5:
                return M(amountToAdd);
            case 6:
                return L(amountToAdd / 256).M((amountToAdd % 256) * 12);
            default:
                return T(this.a.g(amountToAdd, unit), this.b);
        }
    }

    private j L(long days) {
        return T(this.a.g(days, k.DAYS), this.b);
    }

    private j M(long hours) {
        return R(this.a, hours, 0, 0, 0);
    }

    private j O(long minutes) {
        return R(this.a, 0, minutes, 0, 0);
    }

    /* access modifiers changed from: package-private */
    public j Q(long seconds) {
        return R(this.a, 0, 0, seconds, 0);
    }

    private j P(long nanos) {
        return R(this.a, 0, 0, 0, nanos);
    }

    private j R(f newDate, long hours, long minutes, long seconds, long nanos) {
        f fVar = newDate;
        if ((hours | minutes | seconds | nanos) == 0) {
            return T(fVar, this.b);
        }
        long curNoD = this.b.Y();
        long totNanos = (nanos % 86400000000000L) + ((seconds % 86400) * NUM) + ((minutes % 1440) * 60000000000L) + ((hours % 24) * 3600000000000L) + curNoD;
        long totDays = (nanos / 86400000000000L) + (seconds / 86400) + (minutes / 1440) + (hours / 24) + CLASSNAMEf.a(totNanos, 86400000000000L);
        long newNoD = CLASSNAMEi.a(totNanos, 86400000000000L);
        return T(fVar.g(totDays, k.DAYS), newNoD == curNoD ? this.b : LocalTime.S(newNoD));
    }

    public m n(o zone) {
        return o.K(this, zone, (p) null);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof i) || z((i) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return e().hashCode() ^ d().hashCode();
    }

    public String toString() {
        return e().toString() + 'T' + d().toString();
    }
}
