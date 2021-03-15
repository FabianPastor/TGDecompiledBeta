package j$.util.stream;

import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.k;
import j$.util.r;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
class CLASSNAMEt2 implements CLASSNAMEu2<Long, r, CLASSNAMEt2>, A2.g {

    /* renamed from: a  reason: collision with root package name */
    private boolean var_a;
    private long b;
    final /* synthetic */ B c;

    CLASSNAMEt2(B b2) {
        this.c = b2;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public void accept(long j) {
        if (this.var_a) {
            this.var_a = false;
        } else {
            j = this.c.applyAsLong(this.b, j);
        }
        this.b = j;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        Q1.c(this, l);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public C g(C c2) {
        c2.getClass();
        return new CLASSNAMEh(this, c2);
    }

    public Object get() {
        return this.var_a ? r.a() : r.d(this.b);
    }

    public void i(CLASSNAMEu2 u2Var) {
        CLASSNAMEt2 t2Var = (CLASSNAMEt2) u2Var;
        if (!t2Var.var_a) {
            accept(t2Var.b);
        }
    }

    public void m() {
    }

    public void n(long j) {
        this.var_a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
