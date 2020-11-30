package j$.util.stream;

import j$.util.Optional;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.n;
import j$.util.k;

/* renamed from: j$.util.stream.f2  reason: case insensitive filesystem */
class CLASSNAMEf2 implements CLASSNAMEu2<T, Optional<T>, CLASSNAMEf2> {
    private boolean a;
    private Object b;
    final /* synthetic */ n c;

    CLASSNAMEf2(n nVar) {
        this.c = nVar;
    }

    public /* synthetic */ void accept(double d) {
        k.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        k.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        k.b(this);
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

    public void i(CLASSNAMEu2 u2Var) {
        CLASSNAMEf2 f2Var = (CLASSNAMEf2) u2Var;
        if (!f2Var.a) {
            accept(f2Var.b);
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
