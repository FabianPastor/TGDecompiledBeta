package j$.util.stream;

import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.CLASSNAMEo;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.S;
import j$.util.function.V;

class M4 extends S4 implements R4, D5 {
    final /* synthetic */ V b;
    final /* synthetic */ S c;
    final /* synthetic */ CLASSNAMEo d;

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ B q(B b2) {
        return A.a(this, b2);
    }

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    M4(V v, S s, CLASSNAMEo oVar) {
        this.b = v;
        this.c = s;
        this.d = oVar;
    }

    public void s(long size) {
        this.a = this.b.get();
    }

    public void accept(int t) {
        this.c.a(this.a, t);
    }

    /* renamed from: a */
    public void l(M4 other) {
        this.a = this.d.a(this.a, other.a);
    }
}
