package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.j1  reason: case insensitive filesystem */
abstract class CLASSNAMEj1 implements CLASSNAMEm3 {
    boolean a;
    boolean b;

    CLASSNAMEj1(CLASSNAMEk1 k1Var) {
        this.b = !k1Var.b;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
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
