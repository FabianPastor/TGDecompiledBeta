package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Optional;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.n;

/* renamed from: j$.util.stream.u4  reason: case insensitive filesystem */
class CLASSNAMEu4 implements J4 {
    private boolean a;
    private Object b;
    final /* synthetic */ n c;

    CLASSNAMEu4(n nVar) {
        this.c = nVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEk.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEk.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEk.b(this);
        throw null;
    }

    public void accept(Object obj) {
        if (this.a) {
            this.a = false;
        } else {
            obj = this.c.apply(this.b, obj);
        }
        this.b = obj;
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public Object get() {
        return this.a ? Optional.empty() : Optional.of(this.b);
    }

    public void i(J4 j4) {
        CLASSNAMEu4 u4Var = (CLASSNAMEu4) j4;
        if (!u4Var.a) {
            accept(u4Var.b);
        }
    }

    public void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = null;
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
