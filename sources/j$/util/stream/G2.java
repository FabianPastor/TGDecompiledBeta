package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;
import j$.util.function.u;
import j$.util.function.z;

class G2 extends U2 implements T2, CLASSNAMEk3 {
    final /* synthetic */ z b;
    final /* synthetic */ u c;
    final /* synthetic */ CLASSNAMEb d;

    G2(z zVar, u uVar, CLASSNAMEb bVar) {
        this.b = zVar;
        this.c = uVar;
        this.d = bVar;
    }

    public void accept(double d2) {
        this.c.accept(this.a, d2);
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
    public /* synthetic */ void accept(Double d2) {
        CLASSNAMEp1.a(this, d2);
    }

    public void h(T2 t2) {
        this.a = this.d.apply(this.a, ((G2) t2).a);
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
