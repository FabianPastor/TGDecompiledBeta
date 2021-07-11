package j$.util.stream;

import j$.time.a;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.o;
import j$.util.stream.A2;

class Z1 implements CLASSNAMEu2<Double, o, Z1>, A2.e {
    private boolean a;
    private double b;
    final /* synthetic */ p c;

    Z1(p pVar) {
        this.c = pVar;
    }

    public void accept(double d) {
        if (this.a) {
            this.a = false;
        } else {
            d = this.c.applyAsDouble(this.b, d);
        }
        this.b = d;
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
        return this.a ? o.a() : o.d(this.b);
    }

    public void h(CLASSNAMEu2 u2Var) {
        Z1 z1 = (Z1) u2Var;
        if (!z1.a) {
            accept(z1.b);
        }
    }

    public q j(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }

    public void l() {
    }

    public void m(long j) {
        this.a = true;
        this.b = 0.0d;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
