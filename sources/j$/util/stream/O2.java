package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;
import j$.util.function.l;

class O2 implements T2, CLASSNAMEl3 {
    private boolean a;
    private int b;
    final /* synthetic */ j c;

    O2(j jVar) {
        this.c = jVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEp1.f(this);
        throw null;
    }

    public void accept(int i) {
        if (this.a) {
            this.a = false;
        } else {
            i = this.c.applyAsInt(this.b, i);
        }
        this.b = i;
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
        return this.a ? CLASSNAMEk.a() : CLASSNAMEk.d(this.b);
    }

    public void h(T2 t2) {
        O2 o2 = (O2) t2;
        if (!o2.a) {
            accept(o2.b);
        }
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }

    public /* synthetic */ void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = 0;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
