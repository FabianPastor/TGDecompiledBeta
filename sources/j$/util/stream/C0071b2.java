package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.function.q;
import j$.util.k;
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
        this.c.accept(this.var_a, d2);
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d2) {
        Q1.a(this, d2);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(CLASSNAMEu2 u2Var) {
        this.var_a = this.d.apply(this.var_a, ((CLASSNAMEb2) u2Var).var_a);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
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
