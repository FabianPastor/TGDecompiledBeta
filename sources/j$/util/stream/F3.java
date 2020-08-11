package j$.util.stream;

import j$.util.U;
import j$.util.function.Consumer;

final class F3 extends G3 implements CLASSNAMEq3 {
    public /* synthetic */ CLASSNAMEv6 b() {
        return CLASSNAMEp3.d(this);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp3.c(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        o((Long[]) objArr, i);
    }

    public /* synthetic */ void o(Long[] lArr, int i) {
        CLASSNAMEp3.a(this, lArr, i);
    }

    F3(CLASSNAMEq3 left, CLASSNAMEq3 right) {
        super(left, right);
    }

    /* renamed from: g */
    public U spliterator() {
        return new W3(this);
    }
}
