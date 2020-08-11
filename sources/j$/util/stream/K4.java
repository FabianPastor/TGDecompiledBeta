package j$.util.stream;

import j$.util.C;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import j$.util.function.z;

class K4 implements R4, D5 {
    private boolean a;
    private int b;
    final /* synthetic */ z c;

    public /* synthetic */ void accept(double d) {
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

    K4(z zVar) {
        this.c = zVar;
    }

    public void s(long size) {
        this.a = true;
        this.b = 0;
    }

    public void accept(int t) {
        if (this.a) {
            this.a = false;
            this.b = t;
            return;
        }
        this.b = this.c.a(this.b, t);
    }

    /* renamed from: c */
    public C get() {
        return this.a ? C.a() : C.d(this.b);
    }

    /* renamed from: a */
    public void l(K4 other) {
        if (!other.a) {
            accept(other.b);
        }
    }
}
