package j$.time.t;

import j$.CLASSNAMEp;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.e;
import j$.time.f;
import j$.time.i;
import j$.time.o;
import j$.time.u.C;
import j$.time.u.w;

public final /* synthetic */ class p {
    public static q e(w temporal) {
        CLASSNAMEp.a(temporal, "temporal");
        q obj = (q) temporal.r(C.a());
        return obj != null ? obj : t.a;
    }

    public static f a(q _this) {
        return _this.G(e.d());
    }

    public static f b(q _this, e clock) {
        CLASSNAMEp.a(clock, "clock");
        return _this.o(LocalDate.Z(clock));
    }

    public static i c(q _this, w temporal) {
        try {
            return _this.o(temporal).u(LocalTime.L(temporal));
        } catch (f ex) {
            throw new f("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + temporal.getClass(), ex);
        }
    }

    public static m d(q _this, i instant, o zone) {
        return o.L(_this, instant, zone);
    }
}
