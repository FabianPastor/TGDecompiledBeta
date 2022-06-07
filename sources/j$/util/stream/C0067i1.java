package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;
import j$.wrappers.E;

/* renamed from: j$.util.stream.i1  reason: case insensitive filesystem */
class CLASSNAMEi1 extends CLASSNAMEj1 implements CLASSNAMEj3 {
    final /* synthetic */ CLASSNAMEk1 c;
    final /* synthetic */ E d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEi1(CLASSNAMEk1 k1Var, E e) {
        super(k1Var);
        this.c = k1Var;
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
        CLASSNAMEo1.a(this, d2);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
