package j$.util.stream;
/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEr2 extends AbstractCLASSNAMEs2 {
    private final Object[] h;

    CLASSNAMEr2(CLASSNAMEr2 CLASSNAMEr2, j$.util.u uVar, long j, long j2) {
        super(CLASSNAMEr2, uVar, j, j2, CLASSNAMEr2.h.length);
        this.h = CLASSNAMEr2.h;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEr2(j$.util.u uVar, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, Object[] objArr) {
        super(uVar, abstractCLASSNAMEy2, objArr.length);
        this.h = objArr;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        int i = this.f;
        if (i < this.g) {
            Object[] objArr = this.h;
            this.f = i + 1;
            objArr[i] = obj;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2
    AbstractCLASSNAMEs2 b(j$.util.u uVar, long j, long j2) {
        return new CLASSNAMEr2(this, uVar, j, j2);
    }
}
