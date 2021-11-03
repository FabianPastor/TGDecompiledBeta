package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.x;
import j$.util.y;

final class X1 extends CLASSNAMEb2 implements CLASSNAMEv1 {
    X1() {
    }

    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEp1.h(this, dArr, i);
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEy2.g;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEv1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.n(this, j, j2, mVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.k(this, consumer);
    }

    public x spliterator() {
        return N.b();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m520spliterator() {
        return N.b();
    }
}
