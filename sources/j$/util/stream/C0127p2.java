package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.function.w;
import j$.util.k;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.p2  reason: case insensitive filesystem */
class CLASSNAMEp2 extends CLASSNAMEv2<R> implements CLASSNAMEu2<Integer, R, CLASSNAMEp2>, A2.f {
    final /* synthetic */ J b;
    final /* synthetic */ H c;
    final /* synthetic */ n d;

    CLASSNAMEp2(J j, H h, n nVar) {
        this.b = j;
        this.c = h;
        this.d = nVar;
    }

    public /* synthetic */ void accept(double d2) {
        k.c(this);
        throw null;
    }

    public void accept(int i) {
        this.c.accept(this.var_a, i);
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(CLASSNAMEu2 u2Var) {
        this.var_a = this.d.apply(this.var_a, ((CLASSNAMEp2) u2Var).var_a);
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
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
