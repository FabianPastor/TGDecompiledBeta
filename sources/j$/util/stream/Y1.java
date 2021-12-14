package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.x;
import j$.util.y;

final class Y1 extends CLASSNAMEb2 implements CLASSNAMEx1 {
    Y1() {
    }

    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEp1.i(this, numArr, i);
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEy2.e;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEx1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.o(this, j, j2, mVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.l(this, consumer);
    }

    public x spliterator() {
        return N.c();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m9spliterator() {
        return N.c();
    }
}
