package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.b;
import j$.util.function.y;

class J2 extends T2 implements S2 {
    final /* synthetic */ y b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ b d;

    J2(y yVar, BiConsumer biConsumer, b bVar) {
        this.b = yVar;
        this.c = biConsumer;
        this.d = bVar;
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
        this.a = this.d.apply(this.a, ((J2) s2).a);
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
