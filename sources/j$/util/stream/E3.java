package j$.util.stream;

import j$.util.S;
import j$.util.function.Consumer;

final class E3 extends G3 implements CLASSNAMEo3 {
    public /* synthetic */ CLASSNAMEv6 b() {
        return CLASSNAMEn3.d(this);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEn3.c(this, consumer);
    }

    public /* synthetic */ void k(Integer[] numArr, int i) {
        CLASSNAMEn3.a(this, numArr, i);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        k((Integer[]) objArr, i);
    }

    E3(CLASSNAMEo3 left, CLASSNAMEo3 right) {
        super(left, right);
    }

    /* renamed from: g */
    public S spliterator() {
        return new V3(this);
    }
}
