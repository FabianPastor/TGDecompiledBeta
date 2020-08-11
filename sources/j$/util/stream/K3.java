package j$.util.stream;

import j$.util.P;
import j$.util.function.C;
import j$.util.function.CLASSNAMEq;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

final class K3 extends CLASSNAMEk6 implements CLASSNAMEm3, CLASSNAMEh3 {
    private boolean g = false;

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        v((Double) obj);
    }

    public /* synthetic */ void e(Double[] dArr, int i) {
        CLASSNAMEl3.a(this, dArr, i);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        e((Double[]) objArr, i);
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    public /* synthetic */ Object[] x(C c) {
        return CLASSNAMEr3.a(this, c);
    }

    K3() {
    }

    /* renamed from: N */
    public P spliterator() {
        return super.spliterator();
    }

    /* renamed from: Q */
    public void j(CLASSNAMEt consumer) {
        super.j(consumer);
    }

    public void s(long size) {
        this.g = true;
        clear();
        D(size);
    }

    public void accept(double i) {
        super.accept(i);
    }

    public void r() {
        this.g = false;
    }

    /* renamed from: P */
    public void f(double[] array, int offset) {
        super.f(array, offset);
    }

    /* renamed from: O */
    public double[] i() {
        return (double[]) super.i();
    }

    public CLASSNAMEm3 b() {
        return this;
    }
}
