package j$.util.stream;

import j$.util.CLASSNAMEz;
import j$.util.function.CLASSNAMEo;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

class C4 implements R4 {
    private boolean a;
    private Object b;
    final /* synthetic */ CLASSNAMEo c;

    public /* synthetic */ void accept(double d) {
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

    C4(CLASSNAMEo oVar) {
        this.c = oVar;
    }

    public void s(long size) {
        this.a = true;
        this.b = null;
    }

    public void accept(Object t) {
        if (this.a) {
            this.a = false;
            this.b = t;
            return;
        }
        this.b = this.c.a(this.b, t);
    }

    /* renamed from: c */
    public CLASSNAMEz get() {
        return this.a ? CLASSNAMEz.a() : CLASSNAMEz.d(this.b);
    }

    /* renamed from: a */
    public void l(C4 other) {
        if (!other.a) {
            accept(other.b);
        }
    }
}
