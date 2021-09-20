package j$.util.stream;

import j$.util.function.k;
import j$.util.function.l;
import j$.wrappers.V;

/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
class CLASSNAMEh1 extends CLASSNAMEk1 implements CLASSNAMEl3 {
    final /* synthetic */ CLASSNAMEl1 c;
    final /* synthetic */ V d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEh1(CLASSNAMEl1 l1Var, V v) {
        super(l1Var);
        this.c = l1Var;
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
        CLASSNAMEp1.b(this, num);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }
}
