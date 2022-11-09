package j$.util.stream;
/* renamed from: j$.util.stream.m1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEm1 extends AbstractCLASSNAMEd {
    private final CLASSNAMEl1 j;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEm1(CLASSNAMEl1 CLASSNAMEl1, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        super(abstractCLASSNAMEy2, uVar);
        this.j = CLASSNAMEl1;
    }

    CLASSNAMEm1(CLASSNAMEm1 CLASSNAMEm1, j$.util.u uVar) {
        super(CLASSNAMEm1, uVar);
        this.j = CLASSNAMEm1.j;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public Object a() {
        boolean z;
        AbstractCLASSNAMEy2 abstractCLASSNAMEy2 = this.a;
        AbstractCLASSNAMEj1 abstractCLASSNAMEj1 = (AbstractCLASSNAMEj1) this.j.c.get();
        abstractCLASSNAMEy2.u0(abstractCLASSNAMEj1, this.b);
        boolean z2 = abstractCLASSNAMEj1.b;
        z = this.j.b.b;
        if (z2 == z) {
            l(Boolean.valueOf(z2));
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public AbstractCLASSNAMEf f(j$.util.u uVar) {
        return new CLASSNAMEm1(this, uVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEd
    protected Object k() {
        boolean z;
        z = this.j.b.b;
        return Boolean.valueOf(!z);
    }
}
