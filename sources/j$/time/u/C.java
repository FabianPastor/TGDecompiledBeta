package j$.time.u;

import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.o;
import j$.time.p;
import j$.time.t.q;

public final class C {
    static final D a = CLASSNAMEc.a;
    static final D b = CLASSNAMEi.a;
    static final D c = CLASSNAMEh.a;
    static final D d = CLASSNAMEg.a;
    static final D e = CLASSNAMEe.a;
    static final D f = CLASSNAMEd.a;
    static final D g = CLASSNAMEf.a;

    public static D n() {
        return a;
    }

    public static D a() {
        return b;
    }

    public static D l() {
        return c;
    }

    public static D m() {
        return e;
    }

    public static D k() {
        return d;
    }

    public static D i() {
        return f;
    }

    public static D j() {
        return g;
    }

    static /* synthetic */ o b(w temporal) {
        return (o) temporal.r(a);
    }

    static /* synthetic */ q c(w temporal) {
        return (q) temporal.r(b);
    }

    static /* synthetic */ E d(w temporal) {
        return (E) temporal.r(c);
    }

    static /* synthetic */ p e(w temporal) {
        if (temporal.h(j.OFFSET_SECONDS)) {
            return p.X(temporal.i(j.OFFSET_SECONDS));
        }
        return null;
    }

    static /* synthetic */ o f(w temporal) {
        o zone = (o) temporal.r(a);
        return zone != null ? zone : (o) temporal.r(d);
    }

    static /* synthetic */ LocalDate g(w temporal) {
        if (temporal.h(j.EPOCH_DAY)) {
            return LocalDate.b0(temporal.f(j.EPOCH_DAY));
        }
        return null;
    }

    static /* synthetic */ LocalTime h(w temporal) {
        if (temporal.h(j.NANO_OF_DAY)) {
            return LocalTime.S(temporal.f(j.NANO_OF_DAY));
        }
        return null;
    }
}
