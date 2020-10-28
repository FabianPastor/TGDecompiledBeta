package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEt;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.p;
import j$.util.function.q;

/* renamed from: j$.util.stream.o4  reason: case insensitive filesystem */
class CLASSNAMEo4 implements J4, CLASSNAMEq5 {
    private boolean a;
    private double b;
    final /* synthetic */ p c;

    CLASSNAMEo4(p pVar) {
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
        return this.a ? CLASSNAMEt.a() : CLASSNAMEt.d(this.b);
    }

    public void i(J4 j4) {
        CLASSNAMEo4 o4Var = (CLASSNAMEo4) j4;
        if (!o4Var.a) {
            accept(o4Var.b);
        }
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = 0.0d;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
