package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.x;
import j$.util.y;

final class P1 extends S1 implements CLASSNAMEv1 {
    P1(CLASSNAMEv1 v1Var, CLASSNAMEv1 v1Var2) {
        super(v1Var, v1Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEp1.h(this, dArr, i);
    }

    /* renamed from: f */
    public double[] c(int i) {
        return new double[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.k(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ CLASSNAMEv1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.n(this, j, j2, mVar);
    }

    public x spliterator() {
        return new CLASSNAMEg2(this);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m530spliterator() {
        return new CLASSNAMEg2(this);
    }
}
