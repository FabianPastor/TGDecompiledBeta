package j$.util.stream;

import j$.util.U;
import j$.util.function.Consumer;
import j$.util.k0;

final class N3 extends P3 implements CLASSNAMEq3 {
    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp3.c(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        o((Long[]) objArr, i);
    }

    public /* synthetic */ void o(Long[] lArr, int i) {
        CLASSNAMEp3.a(this, lArr, i);
    }

    N3() {
    }

    /* renamed from: g */
    public U spliterator() {
        return k0.d();
    }

    /* renamed from: b */
    public long[] i() {
        return CLASSNAMEp4.f;
    }
}
