package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.p;
import j$.util.function.q;

/* renamed from: j$.util.stream.m4  reason: case insensitive filesystem */
class CLASSNAMEm4 implements J4, CLASSNAMEq5 {
    private double a;
    final /* synthetic */ double b;
    final /* synthetic */ p c;

    CLASSNAMEm4(double d, p pVar) {
        this.b = d;
        this.c = pVar;
    }

    public void accept(double d) {
        this.a = this.c.applyAsDouble(this.a, d);
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEc3.a(this, d);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public Object get() {
        return Double.valueOf(this.a);
    }

    public void i(J4 j4) {
        accept(((CLASSNAMEm4) j4).a);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = this.b;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
