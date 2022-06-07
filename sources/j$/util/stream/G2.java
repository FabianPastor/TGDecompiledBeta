package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.b;

class G2 extends T2 implements S2 {
    final /* synthetic */ Object b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ b d;

    G2(Object obj, BiFunction biFunction, b bVar) {
        this.b = obj;
        this.c = biFunction;
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
        this.a = this.c.apply(this.a, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((G2) s2).a);
    }

    public /* synthetic */ void m() {
    }

    public void n(long j) {
        this.a = this.b;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
