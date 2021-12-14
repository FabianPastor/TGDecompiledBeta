package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.o;
import j$.util.function.p;
import j$.util.function.q;

class R2 implements T2, CLASSNAMEm3 {
    private long a;
    final /* synthetic */ long b;
    final /* synthetic */ o c;

    R2(long j, o oVar) {
        this.b = j;
        this.c = oVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEp1.d(this);
        throw null;
    }

    public void accept(long j) {
        this.a = this.c.applyAsLong(this.a, j);
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

    public Object get() {
        return Long.valueOf(this.a);
    }

    public void h(T2 t2) {
        accept(((R2) t2).a);
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
