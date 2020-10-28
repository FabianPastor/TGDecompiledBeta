package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.v;
import java.util.Arrays;

final class L5 extends I1 {
    L5(CLASSNAMEh1 h1Var) {
        super(h1Var, CLASSNAMEh6.DOUBLE_VALUE, CLASSNAMEg6.v | CLASSNAMEg6.t);
    }

    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        if (CLASSNAMEg6.SORTED.d(i4Var.r0())) {
            return i4Var.o0(spliterator, false, vVar);
        }
        double[] dArr = (double[]) ((CLASSNAMEh3) i4Var.o0(spliterator, true, vVar)).e();
        Arrays.sort(dArr);
        return new A3(dArr);
    }

    public CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        if (CLASSNAMEg6.SORTED.d(i)) {
            return t5Var;
        }
        return CLASSNAMEg6.SIZED.d(i) ? new Q5(t5Var) : new I5(t5Var);
    }
}
