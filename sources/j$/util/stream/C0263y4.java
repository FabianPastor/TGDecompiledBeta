package j$.util.stream;

import j$.util.function.CLASSNAMEo;
import j$.util.function.CLASSNAMEq;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import j$.util.function.Q;
import j$.util.function.V;

/* renamed from: j$.util.stream.y4  reason: case insensitive filesystem */
class CLASSNAMEy4 extends S4 implements R4, B5 {
    final /* synthetic */ V b;
    final /* synthetic */ Q c;
    final /* synthetic */ CLASSNAMEo d;

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEv5.b(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        v((Double) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    public /* synthetic */ void v(Double d2) {
        A5.a(this, d2);
    }

    CLASSNAMEy4(V v, Q q, CLASSNAMEo oVar) {
        this.b = v;
        this.c = q;
        this.d = oVar;
    }

    public void s(long size) {
        this.a = this.b.get();
    }

    public void accept(double t) {
        this.c.a(this.a, t);
    }

    /* renamed from: a */
    public void l(CLASSNAMEy4 other) {
        this.a = this.d.a(this.a, other.a);
    }
}
