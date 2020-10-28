package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.v;

final class E3 extends H3 implements CLASSNAMEi3 {
    E3() {
    }

    /* renamed from: a */
    public /* synthetic */ void j(Integer[] numArr, int i) {
        CLASSNAMEc3.f(this, numArr, i);
    }

    public CLASSNAMEk3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEh4.e;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEi3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.l(this, j, j2, vVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.i(this, consumer);
    }

    public F spliterator() {
        return V.c();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m13spliterator() {
        return V.c();
    }
}
