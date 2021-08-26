package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;
import j$.wrappers.E;

/* renamed from: j$.util.stream.j1  reason: case insensitive filesystem */
class CLASSNAMEj1 extends CLASSNAMEk1 implements CLASSNAMEk3 {
    final /* synthetic */ CLASSNAMEl1 c;
    final /* synthetic */ E d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEj1(CLASSNAMEl1 l1Var, E e) {
        super(l1Var);
        this.c = l1Var;
        this.d = e;
    }

    public void accept(double d2) {
        if (!this.a && this.d.b(d2) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d2) {
        CLASSNAMEp1.a(this, d2);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
