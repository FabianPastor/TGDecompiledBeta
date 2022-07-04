package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.b;
import j$.util.function.e;
import j$.util.function.f;
import j$.util.function.u;
import j$.util.function.y;

class F2 extends T2 implements S2, CLASSNAMEj3 {
    final /* synthetic */ y b;
    final /* synthetic */ u c;
    final /* synthetic */ b d;

    F2(y yVar, u uVar, b bVar) {
        this.b = yVar;
        this.c = uVar;
        this.d = bVar;
    }

    public void accept(double d2) {
        this.c.accept(this.a, d2);
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
    public /* synthetic */ void accept(Double d2) {
        CLASSNAMEo1.a(this, d2);
    }

    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((F2) s2).a);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
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
