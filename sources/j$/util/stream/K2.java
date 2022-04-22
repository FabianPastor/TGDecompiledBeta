package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.b;
import j$.util.function.y;

class K2 extends U2 implements T2 {
    final /* synthetic */ y b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ b d;

    K2(y yVar, BiConsumer biConsumer, b bVar) {
        this.b = yVar;
        this.c = biConsumer;
        this.d = bVar;
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
        this.a = this.d.apply(this.a, ((K2) t2).a);
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
