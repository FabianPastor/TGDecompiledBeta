package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.v;

final class D3 extends H3 implements CLASSNAMEh3 {
    D3() {
    }

    /* renamed from: a */
    public /* synthetic */ void j(Double[] dArr, int i) {
        CLASSNAMEc3.e(this, dArr, i);
    }

    public CLASSNAMEk3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEh4.g;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEh3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.k(this, j, j2, vVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.h(this, consumer);
    }

    public F spliterator() {
        return V.b();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m12spliterator() {
        return V.b();
    }
}
