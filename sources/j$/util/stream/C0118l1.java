package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.l1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEl1 implements N4 {
    private final EnumCLASSNAMEe4 a;
    final EnumCLASSNAMEk1 b;
    final j$.util.function.y c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEl1(EnumCLASSNAMEe4 enumCLASSNAMEe4, EnumCLASSNAMEk1 enumCLASSNAMEk1, j$.util.function.y yVar) {
        this.a = enumCLASSNAMEe4;
        this.b = enumCLASSNAMEk1;
        this.c = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumCLASSNAMEd4.u | EnumCLASSNAMEd4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        return (Boolean) new CLASSNAMEm1(this, abstractCLASSNAMEy2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        AbstractCLASSNAMEj1 abstractCLASSNAMEj1 = (AbstractCLASSNAMEj1) this.c.get();
        AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) abstractCLASSNAMEy2;
        abstractCLASSNAMEj1.getClass();
        abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(abstractCLASSNAMEj1), uVar);
        return Boolean.valueOf(abstractCLASSNAMEj1.b);
    }
}
