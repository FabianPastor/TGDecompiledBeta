package j$.util.stream;

import j$.util.Comparator$CC;
import j$.util.function.m;
import j$.util.u;
import java.util.Arrays;
import java.util.Comparator;

final class M3 extends CLASSNAMEc3 {
    private final boolean l;
    private final Comparator m;

    M3(CLASSNAMEc cVar) {
        super(cVar, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.q | CLASSNAMEd4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    M3(CLASSNAMEc cVar, Comparator comparator) {
        super(cVar, CLASSNAMEe4.REFERENCE, CLASSNAMEd4.q | CLASSNAMEd4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    public A1 E0(CLASSNAMEy2 y2Var, u uVar, m mVar) {
        if (CLASSNAMEd4.SORTED.d(y2Var.s0()) && this.l) {
            return y2Var.p0(uVar, false, mVar);
        }
        Object[] q = y2Var.p0(uVar, true, mVar).q(mVar);
        Arrays.sort(q, this.m);
        return new D1(q);
    }

    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        return (!CLASSNAMEd4.SORTED.d(i) || !this.l) ? CLASSNAMEd4.SIZED.d(i) ? new R3(m3Var, this.m) : new N3(m3Var, this.m) : m3Var;
    }
}
