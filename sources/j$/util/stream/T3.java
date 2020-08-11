package j$.util.stream;

import j$.util.S;
import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

final class T3 extends CLASSNAMEm6 implements CLASSNAMEo3, CLASSNAMEi3 {
    private boolean g = false;

    public /* synthetic */ void accept(double d) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ void k(Integer[] numArr, int i) {
        CLASSNAMEn3.a(this, numArr, i);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        k((Integer[]) objArr, i);
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
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

    T3() {
    }

    /* renamed from: N */
    public S spliterator() {
        return super.spliterator();
    }

    /* renamed from: Q */
    public void j(B consumer) {
        super.j(consumer);
    }

    public void s(long size) {
        this.g = true;
        clear();
        D(size);
    }

    public void accept(int i) {
        super.accept(i);
    }

    public void r() {
        this.g = false;
    }

    /* renamed from: P */
    public void f(int[] array, int offset) {
        super.f(array, offset);
    }

    /* renamed from: O */
    public int[] i() {
        return (int[]) super.i();
    }

    public CLASSNAMEo3 b() {
        return this;
    }
}
