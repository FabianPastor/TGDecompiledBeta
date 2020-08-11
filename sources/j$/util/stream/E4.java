package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEo;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.V;

class E4 extends S4 implements R4 {
    final /* synthetic */ V b;
    final /* synthetic */ BiConsumer c;
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

    E4(V v, BiConsumer biConsumer, CLASSNAMEo oVar) {
        this.b = v;
        this.c = biConsumer;
        this.d = oVar;
    }

    public void s(long size) {
        this.a = this.b.get();
    }

    public void accept(Object t) {
        this.c.accept(this.a, t);
    }

    /* renamed from: a */
    public void l(E4 other) {
        this.a = this.d.a(this.a, other.a);
    }
}
