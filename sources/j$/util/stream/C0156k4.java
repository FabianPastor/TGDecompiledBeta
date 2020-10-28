package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.h;
import j$.util.function.n;
import j$.util.function.y;

/* renamed from: j$.util.stream.k4  reason: case insensitive filesystem */
class CLASSNAMEk4 extends K4 implements J4, CLASSNAMEs5 {
    final /* synthetic */ E b;
    final /* synthetic */ D c;
    final /* synthetic */ n d;

    CLASSNAMEk4(E e, D d2, n nVar) {
        this.b = e;
        this.c = d2;
        this.d = nVar;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public void accept(long j) {
        this.c.accept(this.a, j);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEc3.c(this, l);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }

    public void i(J4 j4) {
        this.a = this.d.apply(this.a, ((CLASSNAMEk4) j4).a);
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
