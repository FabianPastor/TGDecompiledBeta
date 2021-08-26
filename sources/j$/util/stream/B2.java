package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.o;
import j$.util.function.p;
import j$.util.function.v;
import j$.util.function.y;

class B2 extends U2 implements T2, CLASSNAMEm3 {
    final /* synthetic */ y b;
    final /* synthetic */ v c;
    final /* synthetic */ CLASSNAMEb d;

    B2(y yVar, v vVar, CLASSNAMEb bVar) {
        this.b = yVar;
        this.c = vVar;
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

    public void accept(long j) {
        this.c.accept(this.a, j);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }

    public void h(T2 t2) {
        this.a = this.d.apply(this.a, ((B2) t2).a);
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
