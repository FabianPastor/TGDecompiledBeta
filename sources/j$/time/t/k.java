package j$.time.t;

import j$.time.LocalTime;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.t;
import j$.time.u.v;
import j$.time.u.x;

public final /* synthetic */ class k {
    public static G g(m _this, B field) {
        if (!(field instanceof j)) {
            return field.M(_this);
        }
        if (field == j.INSTANT_SECONDS || field == j.OFFSET_SECONDS) {
            return field.p();
        }
        return _this.B().p(field);
    }

    public static int c(m _this, B field) {
        if (!(field instanceof j)) {
            return v.a(_this, field);
        }
        int i = l.a[((j) field).ordinal()];
        if (i == 1) {
            throw new F("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        } else if (i != 2) {
            return _this.B().i(field);
        } else {
            return _this.l().U();
        }
    }

    public static long e(m _this, B field) {
        if (!(field instanceof j)) {
            return field.A(_this);
        }
        int i = l.a[((j) field).ordinal()];
        if (i == 1) {
            return _this.toEpochSecond();
        }
        if (i != 2) {
            return _this.B().f(field);
        }
        return (long) _this.l().U();
    }

    public static f i(m _this) {
        return _this.B().e();
    }

    public static LocalTime j(m _this) {
        return _this.B().d();
    }

    public static q d(m _this) {
        return _this.e().b();
    }

    public static m k(m _this, x adjuster) {
        return o.A(_this.b(), t.a(_this, adjuster));
    }

    public static Object f(m _this, D d) {
        if (d == C.m() || d == C.n()) {
            return _this.s();
        }
        if (d == C.k()) {
            return _this.l();
        }
        if (d == C.j()) {
            return _this.d();
        }
        if (d == C.a()) {
            return _this.b();
        }
        if (d == C.l()) {
            return j$.time.u.k.NANOS;
        }
        return d.a(_this);
    }

    public static long h(m _this) {
        return ((86400 * _this.e().toEpochDay()) + ((long) _this.d().Z())) - ((long) _this.l().U());
    }

    public static int a(m _this, m mVar) {
        int cmp = (_this.toEpochSecond() > mVar.toEpochSecond() ? 1 : (_this.toEpochSecond() == mVar.toEpochSecond() ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        int cmp2 = _this.d().O() - mVar.d().O();
        if (cmp2 != 0) {
            return cmp2;
        }
        int cmp3 = _this.B().z(mVar.B());
        if (cmp3 != 0) {
            return cmp3;
        }
        int cmp4 = _this.s().getId().compareTo(mVar.s().getId());
        if (cmp4 == 0) {
            return _this.b().j(mVar.b());
        }
        return cmp4;
    }
}
