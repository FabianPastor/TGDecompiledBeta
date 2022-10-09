package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class I0 extends L0 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public I0(j$.util.u uVar, int i, boolean z) {
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

    @Override // j$.util.stream.L0, j$.util.stream.IntStream
    public void I(j$.util.function.l lVar) {
        if (!isParallel()) {
            L0.L0(J0()).c(lVar);
            return;
        }
        lVar.getClass();
        x0(new CLASSNAMEl0(lVar, true));
    }

    @Override // j$.util.stream.L0, j$.util.stream.IntStream
    public void U(j$.util.function.l lVar) {
        if (!isParallel()) {
            L0.L0(J0()).c(lVar);
        } else {
            super.U(lVar);
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* bridge */ /* synthetic */ IntStream mo304parallel() {
        mo304parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* bridge */ /* synthetic */ IntStream mo305sequential() {
        mo305sequential();
        return this;
    }
}
