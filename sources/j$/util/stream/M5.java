package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.v;
import java.util.Arrays;

final class M5 extends CLASSNAMEu2 {
    M5(CLASSNAMEh1 h1Var) {
        super(h1Var, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.v | CLASSNAMEg6.t);
    }

    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        if (CLASSNAMEg6.SORTED.d(i4Var.r0())) {
            return i4Var.o0(spliterator, false, vVar);
        }
        int[] iArr = (int[]) ((CLASSNAMEi3) i4Var.o0(spliterator, true, vVar)).e();
        Arrays.sort(iArr);
        return new J3(iArr);
    }

    public CLASSNAMEt5 G0(int i, CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        if (CLASSNAMEg6.SORTED.d(i)) {
            return t5Var;
        }
        return CLASSNAMEg6.SIZED.d(i) ? new R5(t5Var) : new J5(t5Var);
    }
}
