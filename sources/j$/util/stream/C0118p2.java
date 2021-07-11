package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.function.w;
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
        a.c(this);
        throw null;
    }

    public void accept(int i) {
        this.c.accept(this.a, i);
    }

    public /* synthetic */ void accept(long j) {
        a.b(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public void h(CLASSNAMEu2 u2Var) {
        this.a = this.d.apply(this.a, ((CLASSNAMEp2) u2Var).a);
    }

    public w k(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
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
