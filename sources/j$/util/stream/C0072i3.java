package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.i3  reason: case insensitive filesystem */
public abstract class CLASSNAMEi3 implements CLASSNAMEm3 {
    protected final CLASSNAMEm3 a;

    public CLASSNAMEi3(CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        this.a = m3Var;
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

    public void m() {
        this.a.m();
    }

    public boolean o() {
        return this.a.o();
    }
}
