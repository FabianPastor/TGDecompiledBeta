package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.b;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.v;
import j$.util.function.y;

class O2 extends T2 implements S2, CLASSNAMEk3 {
    final /* synthetic */ y b;
    final /* synthetic */ v c;
    final /* synthetic */ b d;

    O2(y yVar, v vVar, b bVar) {
        this.b = yVar;
        this.c = vVar;
        this.d = bVar;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEo1.f(this);
        throw null;
    }

    public void accept(int i) {
        this.c.accept(this.a, i);
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEo1.b(this, num);
    }

    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((O2) s2).a);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
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
