package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.k1  reason: case insensitive filesystem */
abstract class CLASSNAMEk1 implements CLASSNAMEn3 {
    boolean a;
    boolean b;

    CLASSNAMEk1(CLASSNAMEl1 l1Var) {
        this.b = !l1Var.b;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public /* synthetic */ void m() {
    }

    public /* synthetic */ void n(long j) {
    }

    public boolean o() {
        return this.a;
    }
}
