package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.i;
import j$.util.function.j;
import j$.util.function.k;

class N2 implements T2, CLASSNAMEl3 {
    private int a;
    final /* synthetic */ int b;
    final /* synthetic */ i c;

    N2(int i, i iVar) {
        this.b = i;
        this.c = iVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public void accept(int i) {
        this.a = this.c.applyAsInt(this.a, i);
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

    public Object get() {
        return Integer.valueOf(this.a);
    }

    public void h(T2 t2) {
        accept(((N2) t2).a);
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
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
