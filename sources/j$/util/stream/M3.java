package j$.util.stream;

import j$.util.S;
import j$.util.function.Consumer;
import j$.util.k0;

final class M3 extends P3 implements CLASSNAMEo3 {
    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEn3.c(this, consumer);
    }

    public /* synthetic */ void k(Integer[] numArr, int i) {
        CLASSNAMEn3.a(this, numArr, i);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        k((Integer[]) objArr, i);
    }

    M3() {
    }

    /* renamed from: g */
    public S spliterator() {
        return k0.c();
    }

    /* renamed from: b */
    public int[] i() {
        return CLASSNAMEp4.e;
    }
}
