package j$.util.stream;

import j$.time.a;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.function.n;

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
        this.c.accept(this.a, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void h(CLASSNAMEu2 u2Var) {
        this.a = this.d.apply(this.a, ((CLASSNAMEh2) u2Var).a);
    }

    public void l() {
    }

    public void m(long j) {
        this.a = this.b.get();
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
