package j$.time.t;

import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;
import j$.time.u.v;

public final /* synthetic */ class r {
    public static boolean d(s _this, B field) {
        if (field instanceof j) {
            if (field == j.ERA) {
                return true;
            }
            return false;
        } else if (field == null || !field.K(_this)) {
            return false;
        } else {
            return true;
        }
    }

    public static G f(s _this, B field) {
        return v.c(_this, field);
    }

    public static int b(s _this, B field) {
        if (field == j.ERA) {
            return _this.getValue();
        }
        return v.a(_this, field);
    }

    public static long c(s _this, B field) {
        if (field == j.ERA) {
            return (long) _this.getValue();
        }
        if (!(field instanceof j)) {
            return field.A(_this);
        }
        throw new F("Unsupported field: " + field);
    }

    public static Object e(s _this, D d) {
        if (d == C.l()) {
            return k.ERAS;
        }
        return v.b(_this, d);
    }

    public static u a(s _this, u temporal) {
        return temporal.c(j.ERA, (long) _this.getValue());
    }
}
