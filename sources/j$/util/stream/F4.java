package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;
import j$.util.u;
import j$.util.y;

final class F4 extends I4 implements u, f {
    double e;

    F4(u uVar, long j, long j2) {
        super(uVar, j, j2);
    }

    F4(u uVar, F4 f4) {
        super(uVar, f4);
    }

    public void accept(double d) {
        this.e = d;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }

    /* access modifiers changed from: protected */
    public y q(y yVar) {
        return new F4((u) yVar, this);
    }

    /* access modifiers changed from: protected */
    public void s(Object obj) {
        ((f) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk4 t(int i) {
        return new CLASSNAMEh4(i);
    }
}
