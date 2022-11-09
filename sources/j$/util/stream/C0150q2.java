package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.q2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEq2 extends AbstractCLASSNAMEs2 implements InterfaceCLASSNAMEl3 {
    private final long[] h;

    CLASSNAMEq2(CLASSNAMEq2 CLASSNAMEq2, j$.util.u uVar, long j, long j2) {
        super(CLASSNAMEq2, uVar, j, j2, CLASSNAMEq2.h.length);
        this.h = CLASSNAMEq2.h;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEq2(j$.util.u uVar, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, long[] jArr) {
        super(uVar, abstractCLASSNAMEy2, jArr.length);
        this.h = jArr;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2, j$.util.stream.InterfaceCLASSNAMEm3, j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public void accept(long j) {
        int i = this.f;
        if (i < this.g) {
            long[] jArr = this.h;
            this.f = i + 1;
            jArr[i] = j;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2
    AbstractCLASSNAMEs2 b(j$.util.u uVar, long j, long j2) {
        return new CLASSNAMEq2(this, uVar, j, j2);
    }

    @Override // j$.util.function.Consumer
    /* renamed from: c */
    public /* synthetic */ void accept(Long l) {
        AbstractCLASSNAMEo1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
