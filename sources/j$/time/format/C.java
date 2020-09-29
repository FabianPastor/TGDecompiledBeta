package j$.time.format;

import j$.CLASSNAMEo;
import j$.time.i;
import j$.time.o;
import j$.time.p;
import j$.time.t.f;
import j$.time.t.q;
import j$.time.t.t;
import j$.time.u.B;
import j$.time.u.D;
import j$.time.u.j;
import j$.time.u.w;
import java.util.Locale;

final class C {
    private w a;
    private DateTimeFormatter b;
    private int c;

    C(w temporal, DateTimeFormatter formatter) {
        this.a = a(temporal, formatter);
        this.b = formatter;
    }

    private static w a(w temporal, DateTimeFormatter formatter) {
        f effectiveDate;
        q overrideChrono = formatter.d();
        o overrideZone = formatter.g();
        if (overrideChrono == null && overrideZone == null) {
            return temporal;
        }
        q temporalChrono = (q) temporal.r(j$.time.u.C.a());
        o temporalZone = (o) temporal.r(j$.time.u.C.n());
        if (CLASSNAMEo.a(overrideChrono, temporalChrono)) {
            overrideChrono = null;
        }
        if (CLASSNAMEo.a(overrideZone, temporalZone)) {
            overrideZone = null;
        }
        if (overrideChrono == null && overrideZone == null) {
            return temporal;
        }
        q effectiveChrono = overrideChrono != null ? overrideChrono : temporalChrono;
        if (overrideZone != null) {
            if (temporal.h(j.INSTANT_SECONDS)) {
                return (effectiveChrono != null ? effectiveChrono : t.a).H(i.L(temporal), overrideZone);
            } else if ((overrideZone.K() instanceof p) && temporal.h(j.OFFSET_SECONDS) && temporal.i(j.OFFSET_SECONDS) != overrideZone.A().d(i.c).U()) {
                throw new j$.time.f("Unable to apply override zone '" + overrideZone + "' because the temporal object being formatted has a different offset but does not represent an instant: " + temporal);
            }
        }
        o effectiveZone = overrideZone != null ? overrideZone : temporalZone;
        if (overrideChrono == null) {
            effectiveDate = null;
        } else if (temporal.h(j.EPOCH_DAY)) {
            effectiveDate = effectiveChrono.o(temporal);
        } else {
            if (!(overrideChrono == t.a && temporalChrono == null)) {
                j[] values = j.values();
                int length = values.length;
                int i = 0;
                while (i < length) {
                    j f = values[i];
                    if (!f.i() || !temporal.h(f)) {
                        i++;
                    } else {
                        throw new j$.time.f("Unable to apply override chronology '" + overrideChrono + "' because the temporal object being formatted contains date fields but does not represent a whole date: " + temporal);
                    }
                }
            }
            effectiveDate = null;
        }
        return new B(effectiveDate, temporal, effectiveChrono, effectiveZone);
    }

    /* access modifiers changed from: package-private */
    public w e() {
        return this.a;
    }

    /* access modifiers changed from: package-private */
    public Locale d() {
        return this.b.f();
    }

    /* access modifiers changed from: package-private */
    public G c() {
        return this.b.e();
    }

    /* access modifiers changed from: package-private */
    public void h() {
        this.c++;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        this.c--;
    }

    /* access modifiers changed from: package-private */
    public Object g(D d) {
        R result = this.a.r(d);
        if (result != null || this.c != 0) {
            return result;
        }
        throw new j$.time.f("Unable to extract value: " + this.a.getClass());
    }

    /* access modifiers changed from: package-private */
    public Long f(B field) {
        try {
            return Long.valueOf(this.a.f(field));
        } catch (j$.time.f ex) {
            if (this.c > 0) {
                return null;
            }
            throw ex;
        }
    }

    public String toString() {
        return this.a.toString();
    }
}
