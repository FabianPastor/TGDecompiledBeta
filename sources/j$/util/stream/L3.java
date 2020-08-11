package j$.util.stream;

import j$.util.P;
import j$.util.function.Consumer;
import j$.util.k0;

final class L3 extends P3 implements CLASSNAMEm3 {
    public /* synthetic */ void e(Double[] dArr, int i) {
        CLASSNAMEl3.a(this, dArr, i);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEl3.c(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        e((Double[]) objArr, i);
    }

    L3() {
    }

    /* renamed from: g */
    public P spliterator() {
        return k0.b();
    }

    /* renamed from: b */
    public double[] i() {
        return CLASSNAMEp4.g;
    }
}
