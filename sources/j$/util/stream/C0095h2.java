package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.k;

/* renamed from: j$.util.stream.h2  reason: case insensitive filesystem */
class CLASSNAMEh2 extends CLASSNAMEv2<I> implements CLASSNAMEu2<T, I, CLASSNAMEh2> {
    final /* synthetic */ J b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ n d;

    CLASSNAMEh2(J j, BiConsumer biConsumer, n nVar) {
        this.b = j;
        this.c = biConsumer;
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
        this.c.accept(this.var_a, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(CLASSNAMEu2 u2Var) {
        this.var_a = this.d.apply(this.var_a, ((CLASSNAMEh2) u2Var).var_a);
    }

    public void m() {
    }

    public void n(long j) {
        this.var_a = this.b.get();
    }

    public /* synthetic */ boolean p() {
        return false;
    }
}
