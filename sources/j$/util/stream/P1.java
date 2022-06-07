package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

final class P1 extends R1 implements CLASSNAMEw1 {
    P1(CLASSNAMEw1 w1Var, CLASSNAMEw1 w1Var2) {
        super(w1Var, w1Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEo1.i(this, numArr, i);
    }

    /* renamed from: f */
    public int[] c(int i) {
        return new int[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEo1.l(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ CLASSNAMEw1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.o(this, j, j2, mVar);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m2spliterator() {
        return new CLASSNAMEg2(this);
    }

    public u spliterator() {
        return new CLASSNAMEg2(this);
    }
}
