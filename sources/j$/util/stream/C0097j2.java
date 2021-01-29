package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.J;
import j$.util.k;

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
        this.c.accept(this.a, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(CLASSNAMEu2 u2Var) {
        this.d.accept(this.a, ((CLASSNAMEj2) u2Var).a);
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
