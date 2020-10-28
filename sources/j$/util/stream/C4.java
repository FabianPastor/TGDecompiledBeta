package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEu;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.g;
import j$.util.function.t;
import j$.util.function.u;

class C4 implements J4, CLASSNAMEr5 {
    private boolean a;
    private int b;
    final /* synthetic */ t c;

    C4(t tVar) {
        this.c = tVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public void accept(int i) {
        if (this.a) {
            this.a = false;
        } else {
            i = this.c.applyAsInt(this.b, i);
        }
        this.b = i;
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
        return this.a ? CLASSNAMEu.a() : CLASSNAMEu.d(this.b);
    }

    public void i(J4 j4) {
        C4 c4 = (C4) j4;
        if (!c4.a) {
            accept(c4.b);
        }
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }

    public void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
