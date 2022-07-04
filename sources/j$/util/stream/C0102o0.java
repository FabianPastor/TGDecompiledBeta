package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;

/* renamed from: j$.util.stream.o0  reason: case insensitive filesystem */
abstract class CLASSNAMEo0 implements N4, O4 {
    private final boolean a;

    protected CLASSNAMEo0(boolean z) {
        this.a = z;
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

    public int b() {
        if (this.a) {
            return 0;
        }
        return CLASSNAMEd4.r;
    }

    public Object c(CLASSNAMEy2 y2Var, u uVar) {
        (this.a ? new CLASSNAMEq0(y2Var, uVar, (CLASSNAMEm3) this) : new CLASSNAMEr0(y2Var, uVar, y2Var.v0(this))).invoke();
        return null;
    }

    public Object d(CLASSNAMEy2 y2Var, u uVar) {
        CLASSNAMEc cVar = (CLASSNAMEc) y2Var;
        cVar.n0(cVar.v0(this), uVar);
        return null;
    }

    public /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    public /* synthetic */ void m() {
    }

    public /* synthetic */ void n(long j) {
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
