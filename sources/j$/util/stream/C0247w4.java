package j$.util.stream;

import j$.util.B;
import j$.util.function.CLASSNAMEq;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import j$.util.function.r;

/* renamed from: j$.util.stream.w4  reason: case insensitive filesystem */
class CLASSNAMEw4 implements R4, B5 {
    private boolean a;
    private double b;
    final /* synthetic */ r c;

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

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }

    CLASSNAMEw4(r rVar) {
        this.c = rVar;
    }

    public void s(long size) {
        this.a = true;
        this.b = 0.0d;
    }

    public void accept(double t) {
        if (this.a) {
            this.a = false;
            this.b = t;
            return;
        }
        this.b = this.c.a(this.b, t);
    }

    /* renamed from: c */
    public B get() {
        return this.a ? B.a() : B.d(this.b);
    }

    /* renamed from: a */
    public void l(CLASSNAMEw4 other) {
        if (!other.a) {
            accept(other.b);
        }
    }
}
