package j$.util.stream;

import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import j$.util.w;

final class X1 extends CLASSNAMEa2 implements CLASSNAMEw1 {
    X1() {
    }

    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEo1.i(this, numArr, i);
    }

    public CLASSNAMEz1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEx2.e;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEw1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.o(this, j, j2, mVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEo1.l(this, consumer);
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public w m572spliterator() {
        return L.c();
    }

    public u spliterator() {
        return L.c();
    }
}
