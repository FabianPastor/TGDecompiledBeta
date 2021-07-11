package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.x;
import j$.util.stream.CLASSNAMEz1;
import j$.util.stream.R1;
import j$.util.stream.S1;
import java.util.Arrays;

final class K2 extends CLASSNAMEz1.j<Integer> {
    K2(CLASSNAMEh1 h1Var) {
        super(h1Var, U2.INT_VALUE, T2.l | T2.j);
    }

    public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
        if (T2.SORTED.d(t1.r0())) {
            return t1.o0(spliterator, false, xVar);
        }
        int[] iArr = (int[]) ((R1.c) t1.o0(spliterator, true, xVar)).e();
        Arrays.sort(iArr);
        return new S1.l(iArr);
    }

    public A2 G0(int i, A2 a2) {
        a2.getClass();
        return T2.SORTED.d(i) ? a2 : T2.SIZED.d(i) ? new P2(a2) : new H2(a2);
    }
}
