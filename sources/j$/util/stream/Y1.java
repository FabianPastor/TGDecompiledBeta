package j$.util.stream;

import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

final class Y1 extends CLASSNAMEa2 implements CLASSNAMEy1 {
    Y1() {
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEo1.j(this, lArr, i);
    }

    public CLASSNAMEz1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEx2.f;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEy1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.p(this, j, j2, mVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEo1.m(this, consumer);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m571spliterator() {
        return L.d();
    }

    public u spliterator() {
        return L.d();
    }
}
