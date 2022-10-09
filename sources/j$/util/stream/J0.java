package j$.util.stream;
/* loaded from: classes2.dex */
abstract class J0 extends L0 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public J0(AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i) {
        super(abstractCLASSNAMEc, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final boolean G0() {
        return true;
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
