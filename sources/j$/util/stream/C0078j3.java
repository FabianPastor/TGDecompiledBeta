package j$.util.stream;

import j$.util.function.Consumer;

/* renamed from: j$.util.stream.j3  reason: case insensitive filesystem */
public abstract class CLASSNAMEj3 implements CLASSNAMEn3 {
    protected final CLASSNAMEn3 a;

    public CLASSNAMEj3(CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        this.a = n3Var;
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

    public void m() {
        this.a.m();
    }

    public boolean o() {
        return this.a.o();
    }
}
