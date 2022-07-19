package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;

/* renamed from: j$.util.stream.g3  reason: case insensitive filesystem */
public abstract class CLASSNAMEg3 implements CLASSNAMEk3 {
    protected final CLASSNAMEm3 a;

    public CLASSNAMEg3(CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        this.a = m3Var;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEo1.b(this, num);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }

    public void m() {
        this.a.m();
    }

    public boolean o() {
        return this.a.o();
    }
}
