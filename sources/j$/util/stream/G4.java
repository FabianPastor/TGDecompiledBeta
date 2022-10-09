package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class G4 extends H4 implements j$.util.v, j$.util.function.q {
    long e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public G4(j$.util.v vVar, long j, long j2) {
        super(vVar, j, j2);
    }

    G4(j$.util.v vVar, G4 g4) {
        super(vVar, g4);
    }

    @Override // j$.util.function.q
    public void accept(long j) {
        this.e = j;
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.l(this, consumer);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.d(this, consumer);
    }

    @Override // j$.util.stream.J4
    protected j$.util.u q(j$.util.u uVar) {
        return new G4((j$.util.v) uVar, this);
    }

    @Override // j$.util.stream.H4
    protected void s(Object obj) {
        ((j$.util.function.q) obj).accept(this.e);
    }

    @Override // j$.util.stream.H4
    protected AbstractCLASSNAMEj4 t(int i) {
        return new CLASSNAMEi4(i);
    }
}
