package j$.time.t;

import j$.CLASSNAMEp;
import j$.time.i;
import j$.time.p;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.j;
import j$.time.u.k;
import j$.time.u.u;

public final /* synthetic */ class h {
    public static q d(i _this) {
        return _this.e().b();
    }

    public static Object g(i _this, D d) {
        if (d == C.n() || d == C.m() || d == C.k()) {
            return null;
        }
        if (d == C.j()) {
            return _this.d();
        }
        if (d == C.a()) {
            return _this.b();
        }
        if (d == C.l()) {
            return k.NANOS;
        }
        return d.a(_this);
    }

    public static u a(i _this, u temporal) {
        return temporal.c(j.EPOCH_DAY, _this.e().toEpochDay()).c(j.NANO_OF_DAY, _this.d().Y());
    }

    public static i i(i _this, p offset) {
        return i.S(_this.w(offset), (long) _this.d().O());
    }

    public static long h(i _this, p offset) {
        CLASSNAMEp.a(offset, "offset");
        return ((86400 * _this.e().toEpochDay()) + ((long) _this.d().Z())) - ((long) offset.U());
    }

    public static int b(i _this, i iVar) {
        int cmp = _this.e().J(iVar.e());
        if (cmp != 0) {
            return cmp;
        }
        int cmp2 = _this.d().compareTo(iVar.d());
        if (cmp2 == 0) {
            return _this.b().j(iVar.b());
        }
        return cmp2;
    }

    public static boolean e(i _this, i iVar) {
        long thisEpDay = _this.e().toEpochDay();
        long otherEpDay = iVar.e().toEpochDay();
        return thisEpDay > otherEpDay || (thisEpDay == otherEpDay && _this.d().Y() > iVar.d().Y());
    }

    public static boolean f(i _this, i iVar) {
        long thisEpDay = _this.e().toEpochDay();
        long otherEpDay = iVar.e().toEpochDay();
        return thisEpDay < otherEpDay || (thisEpDay == otherEpDay && _this.d().Y() < iVar.d().Y());
    }
}
