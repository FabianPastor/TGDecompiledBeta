package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.H;
import j$.util.function.I;
import j$.util.function.J;

class O4 implements R4, F5 {
    private long a;
    final /* synthetic */ long b;
    final /* synthetic */ H c;

    public /* synthetic */ void accept(double d) {
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

    O4(long j, H h) {
        this.b = j;
        this.c = h;
    }

    public void s(long size) {
        this.a = this.b;
    }

    public void accept(long t) {
        this.a = this.c.a(this.a, t);
    }

    /* renamed from: c */
    public Long get() {
        return Long.valueOf(this.a);
    }

    /* renamed from: a */
    public void l(O4 other) {
        accept(other.a);
    }
}
