package j$.time.t;

import j$.CLASSNAMEp;
import j$.time.LocalDateTime;
import j$.time.LocalTime;
import j$.time.i;
import j$.time.p;
import j$.time.u.B;
import j$.time.u.D;
import j$.time.u.E;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.x;
import j$.time.v.a;
import j$.time.v.c;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTimeImpl;
import java.time.chrono.ChronoZonedDateTimeImpl;
import java.util.List;

final class o implements m, Serializable {
    private final transient j a;
    private final transient p b;
    private final transient j$.time.o c;

    public /* synthetic */ q b() {
        return k.d(this);
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return q((m) obj);
    }

    public /* synthetic */ LocalTime d() {
        return k.j(this);
    }

    public /* synthetic */ f e() {
        return k.i(this);
    }

    public /* synthetic */ long f(B b2) {
        return k.e(this, b2);
    }

    public /* synthetic */ int i(B b2) {
        return k.c(this, b2);
    }

    public /* synthetic */ G p(B b2) {
        return k.g(this, b2);
    }

    public /* synthetic */ int q(m mVar) {
        return k.a(this, mVar);
    }

    public /* synthetic */ Object r(D d) {
        return k.f(this, d);
    }

    public /* synthetic */ long toEpochSecond() {
        return k.h(this);
    }

    static m K(ChronoLocalDateTimeImpl<R> localDateTime, j$.time.o zone, p preferredOffset) {
        p offset;
        CLASSNAMEp.a(localDateTime, "localDateTime");
        CLASSNAMEp.a(zone, "zone");
        if (zone instanceof p) {
            return new o(localDateTime, (p) zone, zone);
        }
        c rules = zone.A();
        LocalDateTime isoLDT = LocalDateTime.M(localDateTime);
        List<ZoneOffset> validOffsets = rules.h(isoLDT);
        if (validOffsets.size() == 1) {
            offset = (p) validOffsets.get(0);
        } else if (validOffsets.size() == 0) {
            a trans = rules.g(isoLDT);
            localDateTime = localDateTime.Q(trans.x().x());
            offset = trans.L();
        } else if (preferredOffset == null || !validOffsets.contains(preferredOffset)) {
            offset = (p) validOffsets.get(0);
        } else {
            offset = preferredOffset;
        }
        CLASSNAMEp.a(offset, "offset");
        return new o(localDateTime, offset, zone);
    }

    static o L(q chrono, i instant, j$.time.o zone) {
        p offset = zone.A().d(instant);
        CLASSNAMEp.a(offset, "offset");
        return new o((j) chrono.v(LocalDateTime.W(instant.M(), instant.O(), offset)), offset, zone);
    }

    private o x(i instant, j$.time.o zone) {
        return L(b(), instant, zone);
    }

    static o A(q chrono, u temporal) {
        ChronoZonedDateTimeImpl<R> other = (o) temporal;
        if (chrono.equals(other.b())) {
            return other;
        }
        throw new ClassCastException("Chronology mismatch, required: " + chrono.getId() + ", actual: " + other.b().getId());
    }

    private o(j dateTime, p offset, j$.time.o zone) {
        CLASSNAMEp.a(dateTime, "dateTime");
        this.a = dateTime;
        CLASSNAMEp.a(offset, "offset");
        this.b = offset;
        CLASSNAMEp.a(zone, "zone");
        this.c = zone;
    }

    public p l() {
        return this.b;
    }

    public i B() {
        return this.a;
    }

    public j$.time.o s() {
        return this.c;
    }

    public boolean h(B field) {
        return (field instanceof j) || (field != null && field.K(this));
    }

    /* renamed from: O */
    public m c(B field, long newValue) {
        if (!(field instanceof j)) {
            return A(b(), field.L(this, newValue));
        }
        j f = (j) field;
        int i = n.a[f.ordinal()];
        if (i == 1) {
            return g(newValue - toEpochSecond(), k.SECONDS);
        }
        if (i != 2) {
            return K(this.a.c(field, newValue), this.c, this.b);
        }
        return x(this.a.S(p.X(f.O(newValue))), this.c);
    }

    /* renamed from: M */
    public m g(long amountToAdd, E unit) {
        if (unit instanceof k) {
            return a((x) this.a.g(amountToAdd, unit));
        }
        return A(b(), unit.p(this, amountToAdd));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof m) || q((m) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((j) B()).hashCode() ^ l().hashCode()) ^ Integer.rotateLeft(s().hashCode(), 3);
    }

    public String toString() {
        String str = ((j) B()).toString() + l().toString();
        if (l() == s()) {
            return str;
        }
        return str + '[' + s().toString() + ']';
    }
}
