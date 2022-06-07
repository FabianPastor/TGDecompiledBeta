package j$.util.stream;

import j$.util.function.m;
import j$.util.u;
import java.util.Arrays;

final class J3 extends Q {
    J3(CLASSNAMEc cVar) {
        super(cVar, CLASSNAMEe4.DOUBLE_VALUE, CLASSNAMEd4.q | CLASSNAMEd4.o);
    }

    public A1 E0(CLASSNAMEy2 y2Var, u uVar, m mVar) {
        if (CLASSNAMEd4.SORTED.d(y2Var.s0())) {
            return y2Var.p0(uVar, false, mVar);
        }
        double[] dArr = (double[]) ((CLASSNAMEu1) y2Var.p0(uVar, true, mVar)).e();
        Arrays.sort(dArr);
        return new T1(dArr);
    }

    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        return CLASSNAMEd4.SORTED.d(i) ? m3Var : CLASSNAMEd4.SIZED.d(i) ? new O3(m3Var) : new G3(m3Var);
    }
}
