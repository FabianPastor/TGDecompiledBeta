package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.n;

/* renamed from: j$.util.stream.w4  reason: case insensitive filesystem */
class CLASSNAMEw4 extends K4 implements J4 {
    final /* synthetic */ E b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ n d;

    CLASSNAMEw4(E e, BiConsumer biConsumer, n nVar) {
        this.b = e;
        this.c = biConsumer;
        this.d = nVar;
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
        this.a = this.d.apply(this.a, ((CLASSNAMEw4) j4).a);
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
