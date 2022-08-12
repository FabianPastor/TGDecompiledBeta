package j$.util.stream;

import j$.util.function.k;
import j$.util.function.l;
import j$.wrappers.V;

/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
class CLASSNAMEg1 extends CLASSNAMEj1 implements CLASSNAMEk3 {
    final /* synthetic */ CLASSNAMEk1 c;
    final /* synthetic */ V d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEg1(CLASSNAMEk1 k1Var, V v) {
        super(k1Var);
        this.c = k1Var;
        this.d = v;
    }

    public void accept(int i) {
        if (!this.a && this.d.b(i) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEo1.b(this, num);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }
}
