package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.b;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.function.y;

class A2 extends T2 implements S2, CLASSNAMEl3 {
    final /* synthetic */ y b;
    final /* synthetic */ w c;
    final /* synthetic */ b d;

    A2(y yVar, w wVar, b bVar) {
        this.b = yVar;
        this.c = wVar;
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

    public void accept(long j) {
        this.c.accept(this.a, j);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEo1.c(this, l);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }

    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((A2) s2).a);
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
