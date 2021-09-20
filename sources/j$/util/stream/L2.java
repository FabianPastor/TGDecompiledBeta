package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.z;

class L2 extends U2 implements T2 {
    final /* synthetic */ z b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ BiConsumer d;

    L2(z zVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = zVar;
        this.c = biConsumer;
        this.d = biConsumer2;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public void accept(Object obj) {
        this.c.accept(this.a, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void h(T2 t2) {
        this.d.accept(this.a, ((L2) t2).a);
    }

    public /* synthetic */ void m() {
    }

    public void n(long j) {
        this.a = this.b.get();
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
