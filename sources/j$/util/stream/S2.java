package j$.util.stream;

import j$.util.CLASSNAMEl;
import j$.util.function.Consumer;
import j$.util.function.n;
import j$.util.function.o;
import j$.util.function.p;

class S2 implements T2, CLASSNAMEm3 {
    private boolean a;
    private long b;
    final /* synthetic */ n c;

    S2(n nVar) {
        this.c = nVar;
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
        if (this.a) {
            this.a = false;
        } else {
            j = this.c.applyAsLong(this.b, j);
        }
        this.b = j;
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

    public Object get() {
        return this.a ? CLASSNAMEl.a() : CLASSNAMEl.d(this.b);
    }

    public void h(T2 t2) {
        S2 s2 = (S2) t2;
        if (!s2.a) {
            accept(s2.b);
        }
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
