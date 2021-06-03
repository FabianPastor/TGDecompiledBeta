package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.x;
import j$.util.stream.D1;
import j$.util.stream.R1;
import j$.util.stream.S1;
import java.util.Arrays;

final class L2 extends D1.h<Long> {
    L2(CLASSNAMEh1 h1Var) {
        super(h1Var, U2.LONG_VALUE, T2.l | T2.j);
    }

    public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
        if (T2.SORTED.d(t1.r0())) {
            return t1.o0(spliterator, false, xVar);
        }
        long[] jArr = (long[]) ((R1.d) t1.o0(spliterator, true, xVar)).e();
        Arrays.sort(jArr);
        return new S1.p(jArr);
    }

    public A2 G0(int i, A2 a2) {
        a2.getClass();
        return T2.SORTED.d(i) ? a2 : T2.SIZED.d(i) ? new Q2(a2) : new I2(a2);
    }
}
