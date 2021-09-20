package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.function.z;

class B2 extends U2 implements T2, CLASSNAMEm3 {
    final /* synthetic */ z b;
    final /* synthetic */ w c;
    final /* synthetic */ CLASSNAMEb d;

    B2(z zVar, w wVar, CLASSNAMEb bVar) {
        this.b = zVar;
        this.c = wVar;
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

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
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
