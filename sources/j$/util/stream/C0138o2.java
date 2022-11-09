package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.o2  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEo2 extends AbstractCLASSNAMEs2 implements InterfaceCLASSNAMEj3 {
    private final double[] h;

    CLASSNAMEo2(CLASSNAMEo2 CLASSNAMEo2, j$.util.u uVar, long j, long j2) {
        super(CLASSNAMEo2, uVar, j, j2, CLASSNAMEo2.h.length);
        this.h = CLASSNAMEo2.h;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEo2(j$.util.u uVar, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, double[] dArr) {
        super(uVar, abstractCLASSNAMEy2, dArr.length);
        this.h = dArr;
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(double d) {
        int i = this.f;
        if (i < this.g) {
            double[] dArr = this.h;
            this.f = i + 1;
            dArr[i] = d;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractCLASSNAMEs2
    AbstractCLASSNAMEs2 b(j$.util.u uVar, long j, long j2) {
        return new CLASSNAMEo2(this, uVar, j, j2);
    }

    @Override // j$.util.function.Consumer
    /* renamed from: c */
    public /* synthetic */ void accept(Double d) {
        AbstractCLASSNAMEo1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
