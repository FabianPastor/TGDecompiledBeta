package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;

class H2 extends U2 implements T2 {
    final /* synthetic */ Object b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ CLASSNAMEb d;

    H2(Object obj, BiFunction biFunction, CLASSNAMEb bVar) {
        this.b = obj;
        this.c = biFunction;
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
        this.a = this.c.apply(this.a, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public void h(T2 t2) {
        this.a = this.d.apply(this.a, ((H2) t2).a);
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
