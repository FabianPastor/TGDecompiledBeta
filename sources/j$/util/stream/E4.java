package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.C;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.g;
import j$.util.function.n;
import j$.util.function.u;

class E4 extends K4 implements J4, CLASSNAMEr5 {
    final /* synthetic */ E b;
    final /* synthetic */ C c;
    final /* synthetic */ n d;

    E4(E e, C c2, n nVar) {
        this.b = e;
        this.c = c2;
        this.d = nVar;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public void accept(int i) {
        this.c.accept(this.a, i);
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(J4 j4) {
        this.a = this.d.apply(this.a, ((E4) j4).a);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
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
