package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class K0 extends L0 {
    /* JADX INFO: Access modifiers changed from: package-private */
    public K0(AbstractCLASSNAMEc abstractCLASSNAMEc, EnumCLASSNAMEe4 enumCLASSNAMEe4, int i) {
        super(abstractCLASSNAMEc, i);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc
    final boolean G0() {
        return false;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    public /* bridge */ /* synthetic */ IntStream mo308parallel() {
        mo308parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEc, j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    public /* bridge */ /* synthetic */ IntStream mo309sequential() {
        mo309sequential();
        return this;
    }
}
