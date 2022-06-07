package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.d;
import j$.util.function.e;
import j$.util.function.f;

class C2 implements S2, CLASSNAMEj3 {
    private double a;
    final /* synthetic */ double b;
    final /* synthetic */ d c;

    C2(double d, d dVar) {
        this.b = d;
        this.c = dVar;
    }

    public void accept(double d) {
        this.a = this.c.applyAsDouble(this.a, d);
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
        return Double.valueOf(this.a);
    }

    public void h(S2 s2) {
        accept(((C2) s2).a);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
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
