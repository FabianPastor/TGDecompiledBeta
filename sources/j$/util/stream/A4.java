package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.t;
import j$.util.function.u;

class A4 implements J4, CLASSNAMEr5 {
    private int a;
    final /* synthetic */ int b;
    final /* synthetic */ t c;

    A4(int i, t tVar) {
        this.b = i;
        this.c = tVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public void accept(int i) {
        this.a = this.c.applyAsInt(this.a, i);
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

    public Object get() {
        return Integer.valueOf(this.a);
    }

    public void i(J4 j4) {
        accept(((A4) j4).a);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
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
