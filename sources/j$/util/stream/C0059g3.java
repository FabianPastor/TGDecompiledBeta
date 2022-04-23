package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.g3  reason: case insensitive filesystem */
public abstract class CLASSNAMEg3 implements CLASSNAMEk3 {
    protected final CLASSNAMEn3 a;

    public CLASSNAMEg3(CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        this.a = n3Var;
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

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEp1.a(this, d);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }

    public void m() {
        this.a.m();
    }

    public boolean o() {
        return this.a.o();
    }
}
