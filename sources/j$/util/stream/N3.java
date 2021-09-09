package j$.util.stream;

import j$.util.Comparator$CC;
import j$.util.function.m;
import j$.util.y;
import java.util.Arrays;
import java.util.Comparator;

final class N3 extends CLASSNAMEd3 {
    private final boolean l;
    private final Comparator m;

    N3(CLASSNAMEc cVar) {
        super(cVar, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.q | CLASSNAMEe4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    N3(CLASSNAMEc cVar, Comparator comparator) {
        super(cVar, CLASSNAMEf4.REFERENCE, CLASSNAMEe4.q | CLASSNAMEe4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    public B1 E0(CLASSNAMEz2 z2Var, y yVar, m mVar) {
        if (CLASSNAMEe4.SORTED.d(z2Var.s0()) && this.l) {
            return z2Var.p0(yVar, false, mVar);
        }
        Object[] q = z2Var.p0(yVar, true, mVar).q(mVar);
        Arrays.sort(q, this.m);
        return new E1(q);
    }

    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        return (!CLASSNAMEe4.SORTED.d(i) || !this.l) ? CLASSNAMEe4.SIZED.d(i) ? new S3(n3Var, this.m) : new O3(n3Var, this.m) : n3Var;
    }
}
