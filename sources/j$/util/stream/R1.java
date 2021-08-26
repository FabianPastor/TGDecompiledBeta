package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.x;
import j$.util.y;

final class R1 extends S1 implements CLASSNAMEz1 {
    R1(CLASSNAMEz1 z1Var, CLASSNAMEz1 z1Var2) {
        super(z1Var, z1Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEp1.j(this, lArr, i);
    }

    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.m(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ CLASSNAMEz1 r(long j, long j2, l lVar) {
        return CLASSNAMEp1.p(this, j, j2, lVar);
    }

    public x spliterator() {
        return new CLASSNAMEi2(this);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m3spliterator() {
        return new CLASSNAMEi2(this);
    }
}
