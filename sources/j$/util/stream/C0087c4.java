package j$.util.stream;

import j$.util.U;
import j$.util.function.C;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.J;

/* renamed from: j$.util.stream.c4  reason: case insensitive filesystem */
final class CLASSNAMEc4 extends CLASSNAMEo6 implements CLASSNAMEq3, CLASSNAMEj3 {
    private boolean g = false;

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        o((Long[]) objArr, i);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    public /* synthetic */ void o(Long[] lArr, int i) {
        CLASSNAMEp3.a(this, lArr, i);
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    public /* synthetic */ Object[] x(C c) {
        return CLASSNAMEr3.a(this, c);
    }

    CLASSNAMEc4() {
    }

    /* renamed from: N */
    public U spliterator() {
        return super.spliterator();
    }

    /* renamed from: Q */
    public void j(J consumer) {
        super.j(consumer);
    }

    public void s(long size) {
        this.g = true;
        clear();
        D(size);
    }

    public void accept(long i) {
        super.accept(i);
    }

    public void r() {
        this.g = false;
    }

    /* renamed from: P */
    public void f(long[] array, int offset) {
        super.f(array, offset);
    }

    /* renamed from: O */
    public long[] i() {
        return (long[]) super.i();
    }

    public CLASSNAMEq3 b() {
        return this;
    }
}
