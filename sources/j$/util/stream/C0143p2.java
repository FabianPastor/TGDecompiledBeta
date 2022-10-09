package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.p2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEp2 extends AbstractCLASSNAMEs2 implements InterfaceCLASSNAMEk3 {
    private final int[] h;

    CLASSNAMEp2(CLASSNAMEp2 CLASSNAMEp2, j$.util.u uVar, long j, long j2) {
        super(CLASSNAMEp2, uVar, j, j2, CLASSNAMEp2.h.length);
        this.h = CLASSNAMEp2.h;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEp2(j$.util.u uVar, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, int[] iArr) {
        super(uVar, abstractCLASSNAMEy2, iArr.length);
        this.h = iArr;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(int i) {
        int i2 = this.f;
        if (i2 < this.g) {
            int[] iArr = this.h;
            this.f = i2 + 1;
            iArr[i2] = i;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2
    AbstractCLASSNAMEs2 b(j$.util.u uVar, long j, long j2) {
        return new CLASSNAMEp2(this, uVar, j, j2);
    }

    @Override // j$.util.function.Consumer
    /* renamed from: c */
    public /* synthetic */ void accept(Integer num) {
        AbstractCLASSNAMEo1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
