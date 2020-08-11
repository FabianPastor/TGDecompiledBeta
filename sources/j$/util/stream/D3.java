package j$.util.stream;

import j$.util.P;
import j$.util.function.Consumer;

final class D3 extends G3 implements CLASSNAMEm3 {
    public /* synthetic */ CLASSNAMEv6 b() {
        return CLASSNAMEl3.d(this);
    }

    public /* synthetic */ void e(Double[] dArr, int i) {
        CLASSNAMEl3.a(this, dArr, i);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEl3.c(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        e((Double[]) objArr, i);
    }

    D3(CLASSNAMEm3 left, CLASSNAMEm3 right) {
        super(left, right);
    }

    /* renamed from: g */
    public P spliterator() {
        return new U3(this);
    }
}
