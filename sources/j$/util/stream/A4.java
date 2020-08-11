package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEo;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

class A4 extends S4 implements R4 {
    final /* synthetic */ Object b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ CLASSNAMEo d;

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    A4(Object obj, BiFunction biFunction, CLASSNAMEo oVar) {
        this.b = obj;
        this.c = biFunction;
        this.d = oVar;
    }

    public void s(long size) {
        this.a = this.b;
    }

    public void accept(Object t) {
        this.a = this.c.a(this.a, t);
    }

    /* renamed from: a */
    public void l(A4 other) {
        this.a = this.d.a(this.a, other.a);
    }
}
