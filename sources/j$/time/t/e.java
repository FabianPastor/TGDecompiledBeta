package j$.time.t;

import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;

public final /* synthetic */ class e {
    public static s c(f _this) {
        return _this.b().N(_this.i(j.ERA));
    }

    public static boolean d(f _this, B field) {
        if (field instanceof j) {
            return field.i();
        }
        return field != null && field.K(_this);
    }

    public static Object e(f _this, D d) {
        if (d == C.n() || d == C.m() || d == C.k() || d == C.j()) {
            return null;
        }
        if (d == C.a()) {
            return _this.b();
        }
        if (d == C.l()) {
            return k.DAYS;
        }
        return d.a(_this);
    }

    public static u a(f _this, u temporal) {
        return temporal.c(j.EPOCH_DAY, _this.toEpochDay());
    }

    public static int b(f _this, f other) {
        int cmp = (_this.toEpochDay() > other.toEpochDay() ? 1 : (_this.toEpochDay() == other.toEpochDay() ? 0 : -1));
        if (cmp == 0) {
            return _this.b().j(other.b());
        }
        return cmp;
    }
}
