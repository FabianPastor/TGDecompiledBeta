package j$.util.stream;

import j$.util.function.CLASSNAMEe;
import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.k;
import j$.util.p;
import j$.util.stream.A2;

class Z1 implements CLASSNAMEu2<Double, p, Z1>, A2.e {

    /* renamed from: a  reason: collision with root package name */
    private boolean var_a;
    private double b;
    final /* synthetic */ j$.util.function.p c;

    Z1(j$.util.function.p pVar) {
        this.c = pVar;
    }

    public void accept(double d) {
        if (this.var_a) {
            this.var_a = false;
        } else {
            d = this.c.applyAsDouble(this.b, d);
        }
        this.b = d;
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
    public /* synthetic */ void accept(Double d) {
        Q1.a(this, d);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public Object get() {
        return this.var_a ? p.a() : p.d(this.b);
    }

    public void i(CLASSNAMEu2 u2Var) {
        Z1 z1 = (Z1) u2Var;
        if (!z1.var_a) {
            accept(z1.b);
        }
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }

    public void m() {
    }

    public void n(long j) {
        this.var_a = true;
        this.b = 0.0d;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
