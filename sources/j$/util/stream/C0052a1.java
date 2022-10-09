package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.a1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public class CLASSNAMEa1 extends AbstractCLASSNAMEd1 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEa1(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final boolean G0() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEc
    public final InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractCLASSNAMEd1, j$.util.stream.InterfaceCLASSNAMEe1
    public void Z(j$.util.function.q qVar) {
        if (!isParallel()) {
            AbstractCLASSNAMEd1.L0(J0()).d(qVar);
            return;
        }
        qVar.getClass();
        x0(new CLASSNAMEm0(qVar, true));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEd1, j$.util.stream.InterfaceCLASSNAMEe1
    public void d(j$.util.function.q qVar) {
        if (!isParallel()) {
            AbstractCLASSNAMEd1.L0(J0()).d(qVar);
        } else {
            super.d(qVar);
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* bridge */ /* synthetic */ InterfaceCLASSNAMEe1 mo304parallel() {
        mo304parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* bridge */ /* synthetic */ InterfaceCLASSNAMEe1 mo305sequential() {
        mo305sequential();
        return this;
    }
}
