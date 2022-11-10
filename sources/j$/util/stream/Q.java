package j$.util.stream;
/* loaded from: classes2.dex */
abstract class Q extends T {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Q(AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i) {
        super(abstractCLASSNAMEc, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* bridge */ /* synthetic */ U moNUMparallel() {
        moNUMparallel();
        return this;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* bridge */ /* synthetic */ U moNUMsequential() {
        moNUMsequential();
        return this;
    }
}
