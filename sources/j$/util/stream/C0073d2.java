package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.n;
import j$.util.k;

/* renamed from: j$.util.stream.d2  reason: case insensitive filesystem */
class CLASSNAMEd2 extends CLASSNAMEv2<U> implements CLASSNAMEu2<T, U, CLASSNAMEd2> {
    final /* synthetic */ Object b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ n d;

    CLASSNAMEd2(Object obj, BiFunction biFunction, n nVar) {
        this.b = obj;
        this.c = biFunction;
        this.d = nVar;
    }

    public /* synthetic */ void accept(double d2) {
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
        this.a = this.c.apply(this.a, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(CLASSNAMEu2 u2Var) {
        this.a = this.d.apply(this.a, ((CLASSNAMEd2) u2Var).a);
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
