package j$.util.stream;

import j$.util.D;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.H;
import j$.util.function.I;
import j$.util.function.J;

class Q4 implements R4, F5 {
    private boolean a;
    private long b;
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

    Q4(H h) {
        this.c = h;
    }

    public void s(long size) {
        this.a = true;
        this.b = 0;
    }

    public void accept(long t) {
        if (this.a) {
            this.a = false;
            this.b = t;
            return;
        }
        this.b = this.c.a(this.b, t);
    }

    /* renamed from: c */
    public D get() {
        return this.a ? D.a() : D.d(this.b);
    }

    /* renamed from: a */
    public void l(Q4 other) {
        if (!other.a) {
            accept(other.b);
        }
    }
}
