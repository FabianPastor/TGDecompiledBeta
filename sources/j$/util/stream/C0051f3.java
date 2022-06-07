package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.f3  reason: case insensitive filesystem */
public abstract class CLASSNAMEf3 implements CLASSNAMEj3 {
    protected final CLASSNAMEm3 a;

    public CLASSNAMEf3(CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        this.a = m3Var;
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

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEo1.a(this, d);
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
