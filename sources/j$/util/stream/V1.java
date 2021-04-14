package j$.util.stream;

import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.k;
import j$.util.stream.A2;

class V1 extends CLASSNAMEv2<R> implements CLASSNAMEu2<Long, R, V1>, A2.g {
    final /* synthetic */ J b;
    final /* synthetic */ I c;
    final /* synthetic */ n d;

    V1(J j, I i, n nVar) {
        this.b = j;
        this.c = i;
        this.d = nVar;
    }

    public /* synthetic */ void accept(double d2) {
        k.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public void accept(long j) {
        this.c.accept(this.var_a, j);
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

    public void i(CLASSNAMEu2 u2Var) {
        this.var_a = this.d.apply(this.var_a, ((V1) u2Var).var_a);
    }

    public void m() {
    }

    public void n(long j) {
        this.var_a = this.b.get();
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
