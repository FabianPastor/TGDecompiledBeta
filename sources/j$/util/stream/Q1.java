package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

final class Q1 extends R1 implements CLASSNAMEy1 {
    Q1(CLASSNAMEy1 y1Var, CLASSNAMEy1 y1Var2) {
        super(y1Var, y1Var2);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEo1.j(this, lArr, i);
    }

    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEo1.m(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ CLASSNAMEy1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.p(this, j, j2, mVar);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m564spliterator() {
        return new CLASSNAMEh2(this);
    }

    public u spliterator() {
        return new CLASSNAMEh2(this);
    }
}
