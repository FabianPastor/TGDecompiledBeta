package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

final class O1 extends R1 implements CLASSNAMEu1 {
    O1(CLASSNAMEu1 u1Var, CLASSNAMEu1 u1Var2) {
        super(u1Var, u1Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEo1.h(this, dArr, i);
    }

    /* renamed from: f */
    public double[] c(int i) {
        return new double[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEo1.k(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ CLASSNAMEu1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.n(this, j, j2, mVar);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m564spliterator() {
        return new CLASSNAMEf2(this);
    }

    public u spliterator() {
        return new CLASSNAMEf2(this);
    }
}
