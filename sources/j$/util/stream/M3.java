package j$.util.stream;

import j$.util.function.l;
import j$.util.y;
import java.util.Arrays;

final class M3 extends CLASSNAMEc1 {
    M3(CLASSNAMEc cVar) {
        super(cVar, CLASSNAMEf4.LONG_VALUE, CLASSNAMEe4.q | CLASSNAMEe4.o);
    }

    public B1 E0(CLASSNAMEz2 z2Var, y yVar, l lVar) {
        if (CLASSNAMEe4.SORTED.d(z2Var.s0())) {
            return z2Var.p0(yVar, false, lVar);
        }
        long[] jArr = (long[]) ((CLASSNAMEz1) z2Var.p0(yVar, true, lVar)).e();
        Arrays.sort(jArr);
        return new CLASSNAMEm2(jArr);
    }

    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        return CLASSNAMEe4.SORTED.d(i) ? n3Var : CLASSNAMEe4.SIZED.d(i) ? new R3(n3Var) : new J3(n3Var);
    }
}
