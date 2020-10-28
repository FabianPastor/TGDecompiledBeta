package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.v;

final class F3 extends H3 implements CLASSNAMEj3 {
    F3() {
    }

    /* renamed from: a */
    public /* synthetic */ void j(Long[] lArr, int i) {
        CLASSNAMEc3.g(this, lArr, i);
    }

    public CLASSNAMEk3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEh4.f;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEj3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.m(this, j, j2, vVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.j(this, consumer);
    }

    public F spliterator() {
        return V.d();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m14spliterator() {
        return V.d();
    }
}
