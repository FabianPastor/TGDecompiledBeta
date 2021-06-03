package j$.util.stream;

import j$.time.a;
import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.function.n;
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
        a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public void accept(long j) {
        this.c.accept(this.a, j);
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

    public void h(CLASSNAMEu2 u2Var) {
        this.a = this.d.apply(this.a, ((V1) u2Var).a);
    }

    public void l() {
    }

    public void m(long j) {
        this.a = this.b.get();
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
