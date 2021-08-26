package j$.util.stream;

import j$.util.CLASSNAMEj;
import j$.util.function.Consumer;
import j$.util.function.d;
import j$.util.function.e;
import j$.util.function.f;

class F2 implements T2, CLASSNAMEk3 {
    private boolean a;
    private double b;
    final /* synthetic */ d c;

    F2(d dVar) {
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
        CLASSNAMEp1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        CLASSNAMEp1.e(this);
        throw null;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEp1.a(this, d);
    }

    public Object get() {
        return this.a ? CLASSNAMEj.a() : CLASSNAMEj.d(this.b);
    }

    public void h(T2 t2) {
        F2 f2 = (F2) t2;
        if (!f2.a) {
            accept(f2.b);
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
