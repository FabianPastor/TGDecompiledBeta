package j$.util.stream;

import j$.util.function.Predicate;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.d0  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEd0 implements N4 {
    private final EnumCLASSNAMEe4 a;
    final boolean b;
    final Object c;
    final Predicate d;
    final j$.util.function.y e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEd0(boolean z, EnumCLASSNAMEe4 enumCLASSNAMEe4, Object obj, Predicate predicate, j$.util.function.y yVar) {
        this.b = z;
        this.a = enumCLASSNAMEe4;
        this.c = obj;
        this.d = predicate;
        this.e = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumCLASSNAMEd4.u | (this.b ? 0 : EnumCLASSNAMEd4.r);
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        return new CLASSNAMEj0(this, abstractCLASSNAMEy2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        O4 o4 = (O4) this.e.get();
        AbstractCLASSNAMEc abstractCLASSNAMEc = (AbstractCLASSNAMEc) abstractCLASSNAMEy2;
        o4.getClass();
        abstractCLASSNAMEc.n0(abstractCLASSNAMEc.v0(o4), uVar);
        Object obj = o4.get();
        return obj != null ? obj : this.c;
    }
}
