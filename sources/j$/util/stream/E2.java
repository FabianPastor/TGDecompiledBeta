package j$.util.stream;

import j$.util.CLASSNAMEj;
import j$.util.function.Consumer;
import j$.util.function.d;
import j$.util.function.e;
import j$.util.function.f;

class E2 implements S2, CLASSNAMEj3 {
    private boolean a;
    private double b;
    final /* synthetic */ d c;

    E2(d dVar) {
        this.c = dVar;
    }

    public void accept(double d) {
        if (this.a) {
            this.a = false;
        } else {
            d = this.c.applyAsDouble(this.b, d);
        }
        this.b = d;
    }

    public /* synthetic */ void accept(int i) {
        CLASSNAMEo1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEo1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEo1.a(this, d);
    }

    public Object get() {
        return this.a ? CLASSNAMEj.a() : CLASSNAMEj.d(this.b);
    }

    public void h(S2 s2) {
        E2 e2 = (E2) s2;
        if (!e2.a) {
            accept(e2.b);
        }
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }

    public /* synthetic */ void m() {
    }

    public void n(long j) {
        this.a = true;
        this.b = 0.0d;
    }

    public /* synthetic */ boolean o() {
        return false;
    }
}
