package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.e;
import j$.util.function.f;
import j$.util.t;
import j$.util.u;

final class E4 extends H4 implements t, f {
    double e;

    E4(t tVar, long j, long j2) {
        super(tVar, j, j2);
    }

    E4(t tVar, E4 e4) {
        super(tVar, e4);
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
    public u q(u uVar) {
        return new E4((t) uVar, this);
    }

    /* access modifiers changed from: protected */
    public void s(Object obj) {
        ((f) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEj4 t(int i) {
        return new CLASSNAMEg4(i);
    }
}
