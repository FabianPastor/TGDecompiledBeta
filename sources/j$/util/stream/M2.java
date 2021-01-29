package j$.util.stream;

import j$.util.Comparator;
import j$.util.Spliterator;
import j$.util.function.x;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.S1;
import java.util.Arrays;
import java.util.Comparator;

final class M2<T> extends CLASSNAMEy2.l<T, T> {
    private final boolean l;
    private final Comparator m;

    M2(CLASSNAMEh1 h1Var) {
        super(h1Var, U2.REFERENCE, T2.q | T2.o);
        this.l = true;
        this.m = Comparator.CC.b();
    }

    M2(CLASSNAMEh1 h1Var, java.util.Comparator comparator) {
        super(h1Var, U2.REFERENCE, T2.q | T2.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
        if (T2.SORTED.d(t1.r0()) && this.l) {
            return t1.o0(spliterator, false, xVar);
        }
        Object[] q = t1.o0(spliterator, true, xVar).q(xVar);
        Arrays.sort(q, this.m);
        return new S1.c(q);
    }

    public A2 G0(int i, A2 a2) {
        a2.getClass();
        if (!T2.SORTED.d(i) || !this.l) {
            return T2.SIZED.d(i) ? new R2(a2, this.m) : new N2(a2, this.m);
        }
        return a2;
    }
}
