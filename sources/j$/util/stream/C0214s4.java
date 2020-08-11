package j$.util.stream;

import j$.util.function.CLASSNAMEo;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.function.T;
import j$.util.function.V;

/* renamed from: j$.util.stream.s4  reason: case insensitive filesystem */
class CLASSNAMEs4 extends S4 implements R4, F5 {
    final /* synthetic */ V b;
    final /* synthetic */ T c;
    final /* synthetic */ CLASSNAMEo d;

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEv5.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEv5.a(this);
        throw null;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    public /* synthetic */ void r() {
        CLASSNAMEv5.f();
    }

    public /* synthetic */ boolean u() {
        CLASSNAMEv5.e();
        return false;
    }

    CLASSNAMEs4(V v, T t, CLASSNAMEo oVar) {
        this.b = v;
        this.c = t;
        this.d = oVar;
    }

    public void s(long size) {
        this.a = this.b.get();
    }

    public void accept(long t) {
        this.c.a(this.a, t);
    }

    /* renamed from: a */
    public void l(CLASSNAMEs4 other) {
        this.a = this.d.a(this.a, other.a);
    }
}
