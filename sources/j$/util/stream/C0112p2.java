package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;
import j$.util.y;

/* renamed from: j$.util.stream.p2  reason: case insensitive filesystem */
final class CLASSNAMEp2 extends CLASSNAMEt2 implements CLASSNAMEk3 {
    private final double[] h;

    CLASSNAMEp2(CLASSNAMEp2 p2Var, y yVar, long j, long j2) {
        super(p2Var, yVar, j, j2, p2Var.h.length);
        this.h = p2Var.h;
    }

    CLASSNAMEp2(y yVar, CLASSNAMEz2 z2Var, double[] dArr) {
        super(yVar, z2Var, dArr.length);
        this.h = dArr;
    }

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

    /* access modifiers changed from: package-private */
    public CLASSNAMEt2 b(y yVar, long j, long j2) {
        return new CLASSNAMEp2(this, yVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEp1.a(this, d);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
