package j$.util.stream;

import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

final class W1 extends CLASSNAMEa2 implements CLASSNAMEu1 {
    W1() {
    }

    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEo1.h(this, dArr, i);
    }

    public CLASSNAMEz1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEx2.g;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEu1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.n(this, j, j2, mVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEo1.k(this, consumer);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m571spliterator() {
        return L.b();
    }

    public u spliterator() {
        return L.b();
    }
}
