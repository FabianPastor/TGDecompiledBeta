package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.B;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.f;
import j$.util.function.n;
import j$.util.function.q;

/* renamed from: j$.util.stream.q4  reason: case insensitive filesystem */
class CLASSNAMEq4 extends K4 implements J4, CLASSNAMEq5 {
    final /* synthetic */ E b;
    final /* synthetic */ B c;
    final /* synthetic */ n d;

    CLASSNAMEq4(E e, B b2, n nVar) {
        this.b = e;
        this.c = b2;
        this.d = nVar;
    }

    public void accept(double d2) {
        this.c.accept(this.a, d2);
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
    public /* synthetic */ void accept(Double d2) {
        CLASSNAMEc3.a(this, d2);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(J4 j4) {
        this.a = this.d.apply(this.a, ((CLASSNAMEq4) j4).a);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = this.b.get();
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
