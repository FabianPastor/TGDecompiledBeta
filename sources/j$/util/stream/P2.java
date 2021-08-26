package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;
import j$.util.function.u;
import j$.util.function.y;

class P2 extends U2 implements T2, CLASSNAMEl3 {
    final /* synthetic */ y b;
    final /* synthetic */ u c;
    final /* synthetic */ CLASSNAMEb d;

    P2(y yVar, u uVar, CLASSNAMEb bVar) {
        this.b = yVar;
        this.c = uVar;
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

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
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
