package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;
import j$.util.function.l;

class N2 implements S2, CLASSNAMEk3 {
    private boolean a;
    private int b;
    final /* synthetic */ j c;

    N2(j jVar) {
        this.c = jVar;
    }

    public /* synthetic */ void accept(double d) {
        CLASSNAMEo1.f(this);
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

    public Object get() {
        return this.a ? CLASSNAMEk.a() : CLASSNAMEk.d(this.b);
    }

    public void h(S2 s2) {
        N2 n2 = (N2) s2;
        if (!n2.a) {
            accept(n2.b);
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
