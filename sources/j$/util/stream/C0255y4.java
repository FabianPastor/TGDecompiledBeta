package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.E;

/* renamed from: j$.util.stream.y4  reason: case insensitive filesystem */
class CLASSNAMEy4 extends K4 implements J4 {
    final /* synthetic */ E b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ BiConsumer d;

    CLASSNAMEy4(E e, BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = e;
        this.c = biConsumer;
        this.d = biConsumer2;
    }

    public /* synthetic */ void accept(double d2) {
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
        this.c.accept(this.a, obj);
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void i(J4 j4) {
        this.d.accept(this.a, ((CLASSNAMEy4) j4).a);
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
