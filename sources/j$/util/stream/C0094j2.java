package j$.util.stream;

import j$.time.a;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.J;

/* renamed from: j$.util.stream.j2  reason: case insensitive filesystem */
class CLASSNAMEj2 extends CLASSNAMEv2<R> implements CLASSNAMEu2<T, R, CLASSNAMEj2> {
    final /* synthetic */ J b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ BiConsumer d;

    CLASSNAMEj2(J j, BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = j;
        this.c = biConsumer;
        this.d = biConsumer2;
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
        this.d.accept(this.a, ((CLASSNAMEj2) u2Var).a);
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
