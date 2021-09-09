package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.x;
import j$.util.y;

final class Q1 extends S1 implements CLASSNAMEx1 {
    Q1(CLASSNAMEx1 x1Var, CLASSNAMEx1 x1Var2) {
        super(x1Var, x1Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEp1.i(this, numArr, i);
    }

    /* renamed from: f */
    public int[] c(int i) {
        return new int[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.l(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ CLASSNAMEx1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.o(this, j, j2, mVar);
    }

    public x spliterator() {
        return new CLASSNAMEh2(this);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m2spliterator() {
        return new CLASSNAMEh2(this);
    }
}
