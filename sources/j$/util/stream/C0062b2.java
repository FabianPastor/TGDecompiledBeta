package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.function.q;
import j$.util.stream.A2;

/* renamed from: j$.util.stream.b2  reason: case insensitive filesystem */
class CLASSNAMEb2 extends CLASSNAMEv2<R> implements CLASSNAMEu2<Double, R, CLASSNAMEb2>, A2.e {
    final /* synthetic */ J b;
    final /* synthetic */ G c;
    final /* synthetic */ n d;

    CLASSNAMEb2(J j, G g, n nVar) {
        this.b = j;
        this.c = g;
        this.d = nVar;
    }

    public void accept(double d2) {
        this.c.accept(this.a, d2);
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        a.b(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d2) {
        Q1.a(this, d2);
    }

    public void h(CLASSNAMEu2 u2Var) {
        this.a = this.d.apply(this.a, ((CLASSNAMEb2) u2Var).a);
    }

    public q j(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
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
