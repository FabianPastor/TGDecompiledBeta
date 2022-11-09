package j$.util.stream;
/* loaded from: classes2.dex */
abstract class U2 implements N4 {
    private final EnumCLASSNAMEe4 a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public U2(EnumCLASSNAMEe4 enumCLASSNAMEe4) {
        this.a = enumCLASSNAMEe4;
    }

    public abstract S2 a();

    @Override // j$.util.stream.N4
    public /* synthetic */ int b() {
        return 0;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        return ((S2) new V2(this, abstractCLASSNAMEy2, uVar).invoke()).get();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        S2 a = a();
        AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) abstractCLASSNAMEy2;
        a.getClass();
        abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(a), uVar);
        return a.get();
    }
}
