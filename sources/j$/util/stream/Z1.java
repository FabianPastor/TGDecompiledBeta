package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.x;
import j$.util.y;

final class Z1 extends CLASSNAMEb2 implements CLASSNAMEz1 {
    Z1() {
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEp1.j(this, lArr, i);
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public Object e() {
        return CLASSNAMEy2.f;
    }

    /* renamed from: f */
    public /* synthetic */ CLASSNAMEz1 r(long j, long j2, l lVar) {
        return CLASSNAMEp1.p(this, j, j2, lVar);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.m(this, consumer);
    }

    public x spliterator() {
        return N.d();
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m10spliterator() {
        return N.d();
    }
}
