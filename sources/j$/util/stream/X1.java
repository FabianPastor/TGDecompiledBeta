package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.stream.A2;

class X1 implements CLASSNAMEu2<Double, Double, X1>, A2.e {
    private double a;
    final /* synthetic */ double b;
    final /* synthetic */ p c;

    X1(double d, p pVar) {
        this.b = d;
        this.c = pVar;
    }

    public void accept(double d) {
        this.a = this.c.applyAsDouble(this.a, d);
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
    public /* synthetic */ void accept(Double d) {
        Q1.a(this, d);
    }

    public Object get() {
        return Double.valueOf(this.a);
    }

    public void h(CLASSNAMEu2 u2Var) {
        accept(((X1) u2Var).a);
    }

    public q j(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }

    public void l() {
    }

    public void m(long j) {
        this.a = this.b;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
