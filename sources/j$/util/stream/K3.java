package j$.util.stream;

import j$.util.function.l;
import j$.util.y;
import java.util.Arrays;

final class K3 extends Q {
    K3(CLASSNAMEc cVar) {
        super(cVar, CLASSNAMEf4.DOUBLE_VALUE, CLASSNAMEe4.q | CLASSNAMEe4.o);
    }

    public B1 E0(CLASSNAMEz2 z2Var, y yVar, l lVar) {
        if (CLASSNAMEe4.SORTED.d(z2Var.s0())) {
            return z2Var.p0(yVar, false, lVar);
        }
        double[] dArr = (double[]) ((CLASSNAMEv1) z2Var.p0(yVar, true, lVar)).e();
        Arrays.sort(dArr);
        return new U1(dArr);
    }

    public CLASSNAMEn3 H0(int i, CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        return CLASSNAMEe4.SORTED.d(i) ? n3Var : CLASSNAMEe4.SIZED.d(i) ? new P3(n3Var) : new H3(n3Var);
    }
}
