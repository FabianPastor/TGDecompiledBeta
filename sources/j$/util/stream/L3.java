package j$.util.stream;

import j$.util.function.m;
import j$.util.y;
import java.util.Arrays;

final class L3 extends J0 {
    L3(CLASSNAMEc cVar) {
        super(cVar, CLASSNAMEf4.INT_VALUE, CLASSNAMEe4.q | CLASSNAMEe4.o);
    }

    public B1 E0(CLASSNAMEz2 z2Var, y yVar, m mVar) {
        if (CLASSNAMEe4.SORTED.d(z2Var.s0())) {
            return z2Var.p0(yVar, false, mVar);
        }
        int[] iArr = (int[]) ((CLASSNAMEx1) z2Var.p0(yVar, true, mVar)).e();
        Arrays.sort(iArr);
        return new CLASSNAMEd2(iArr);
    }

    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        return CLASSNAMEe4.SORTED.d(i) ? n3Var : CLASSNAMEe4.SIZED.d(i) ? new Q3(n3Var) : new I3(n3Var);
    }
}