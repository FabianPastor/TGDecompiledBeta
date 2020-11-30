package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.x;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.R1;
import j$.util.stream.S1;
import java.util.Arrays;

final class J2 extends CLASSNAMEp1.h<Double> {
    J2(CLASSNAMEh1 h1Var) {
        super(h1Var, U2.DOUBLE_VALUE, T2.q | T2.o);
    }

    public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
        if (T2.SORTED.d(t1.r0())) {
            return t1.o0(spliterator, false, xVar);
        }
        double[] dArr = (double[]) ((R1.b) t1.o0(spliterator, true, xVar)).e();
        Arrays.sort(dArr);
        return new S1.g(dArr);
    }

    public A2 G0(int i, A2 a2) {
        a2.getClass();
        if (T2.SORTED.d(i)) {
            return a2;
        }
        return T2.SIZED.d(i) ? new O2(a2) : new G2(a2);
    }
}
