package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;

/* renamed from: j$.util.stream.i3  reason: case insensitive filesystem */
public abstract class CLASSNAMEi3 implements CLASSNAMEm3 {
    protected final CLASSNAMEn3 a;

    public CLASSNAMEi3(CLASSNAMEn3 n3Var) {
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

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }

    public void m() {
        this.a.m();
    }

    public boolean o() {
        return this.a.o();
    }
}
