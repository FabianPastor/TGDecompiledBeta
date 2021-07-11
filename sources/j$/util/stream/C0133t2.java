package j$.util.stream;

import j$.time.a;
import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.q;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
class CLASSNAMEt2 implements CLASSNAMEu2<Long, q, CLASSNAMEt2>, A2.g {
    private boolean a;
    private long b;
    final /* synthetic */ B c;

    CLASSNAMEt2(B b2) {
        this.c = b2;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public void accept(long j) {
        if (this.a) {
            this.a = false;
        } else {
            j = this.c.applyAsLong(this.b, j);
        }
        this.b = j;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        Q1.c(this, l);
    }

    public C f(C c2) {
        c2.getClass();
        return new CLASSNAMEh(this, c2);
    }

    public Object get() {
        return this.a ? q.a() : q.d(this.b);
    }

    public void h(CLASSNAMEu2 u2Var) {
        CLASSNAMEt2 t2Var = (CLASSNAMEt2) u2Var;
        if (!t2Var.a) {
            accept(t2Var.b);
        }
    }

    public void l() {
    }

    public void m(long j) {
        this.a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
