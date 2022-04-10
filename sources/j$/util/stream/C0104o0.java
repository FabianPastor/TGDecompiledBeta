package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.y;

/* renamed from: j$.util.stream.o0  reason: case insensitive filesystem */
abstract class CLASSNAMEo0 implements O4, P4 {
    private final boolean a;

    protected CLASSNAMEo0(boolean z) {
        this.a = z;
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

    public int b() {
        if (this.a) {
            return 0;
        }
        return CLASSNAMEe4.r;
    }

    public Object c(CLASSNAMEz2 z2Var, y yVar) {
        (this.a ? new CLASSNAMEq0(z2Var, yVar, (CLASSNAMEn3) this) : new CLASSNAMEr0(z2Var, yVar, z2Var.v0(this))).invoke();
        return null;
    }

    public Object d(CLASSNAMEz2 z2Var, y yVar) {
        CLASSNAMEc cVar = (CLASSNAMEc) z2Var;
        cVar.n0(cVar.v0(this), yVar);
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
