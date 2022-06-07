package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.y;

class K2 extends T2 implements S2 {
    final /* synthetic */ y b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ BiConsumer d;

    K2(y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.b = yVar;
        this.c = biConsumer;
        this.d = biConsumer2;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public void accept(Object obj) {
        this.c.accept(this.a, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void h(S2 s2) {
        this.d.accept(this.a, ((K2) s2).a);
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
