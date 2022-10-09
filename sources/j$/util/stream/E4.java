package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class E4 extends H4 implements j$.util.t, j$.util.function.f {
    double e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public E4(j$.util.t tVar, long j, long j2) {
        super(tVar, j, j2);
    }

    E4(j$.util.t tVar, E4 e4) {
        super(tVar, e4);
    }

    @Override // j$.util.function.f
    public void accept(double d) {
        this.e = d;
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.j(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.b(this, consumer);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.stream.J4
    protected j$.util.u q(j$.util.u uVar) {
        return new E4((j$.util.t) uVar, this);
    }

    @Override // j$.util.stream.H4
    protected void s(Object obj) {
        ((j$.util.function.f) obj).accept(this.e);
    }

    @Override // j$.util.stream.H4
    protected AbstractCLASSNAMEj4 t(int i) {
        return new CLASSNAMEg4(i);
    }
}
