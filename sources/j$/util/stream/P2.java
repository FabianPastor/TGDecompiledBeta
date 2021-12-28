package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.v;
import j$.util.function.y;

class P2 extends U2 implements T2, CLASSNAMEl3 {
    final /* synthetic */ y b;
    final /* synthetic */ v c;
    final /* synthetic */ CLASSNAMEb d;

    P2(y yVar, v vVar, CLASSNAMEb bVar) {
        this.b = yVar;
        this.c = vVar;
        this.d = bVar;
    }

    public /* synthetic */ void accept(double d2) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public void accept(int i) {
        this.c.accept(this.a, i);
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEp1.b(this, num);
    }

    public void h(T2 t2) {
        this.a = this.d.apply(this.a, ((P2) t2).a);
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
