package j$.util.stream;

import j$.time.a;
import j$.util.Optional;
import j$.util.function.Consumer;
import j$.util.function.n;

/* renamed from: j$.util.stream.f2  reason: case insensitive filesystem */
class CLASSNAMEf2 implements CLASSNAMEu2<T, Optional<T>, CLASSNAMEf2> {
    private boolean a;
    private Object b;
    final /* synthetic */ n c;

    CLASSNAMEf2(n nVar) {
        this.c = nVar;
    }

    public /* synthetic */ void accept(double d) {
        a.c(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        a.a(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        a.b(this);
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

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public Object get() {
        return this.a ? Optional.empty() : Optional.of(this.b);
    }

    public void h(CLASSNAMEu2 u2Var) {
        CLASSNAMEf2 f2Var = (CLASSNAMEf2) u2Var;
        if (!f2Var.a) {
            accept(f2Var.b);
        }
    }

    public void l() {
    }

    public void m(long j) {
        this.a = true;
        this.b = null;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
