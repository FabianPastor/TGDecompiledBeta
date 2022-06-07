package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;
import j$.util.u;

/* renamed from: j$.util.stream.o2  reason: case insensitive filesystem */
final class CLASSNAMEo2 extends CLASSNAMEs2 implements CLASSNAMEj3 {
    private final double[] h;

    CLASSNAMEo2(CLASSNAMEo2 o2Var, u uVar, long j, long j2) {
        super(o2Var, uVar, j, j2, o2Var.h.length);
        this.h = o2Var.h;
    }

    CLASSNAMEo2(u uVar, CLASSNAMEy2 y2Var, double[] dArr) {
        super(uVar, y2Var, dArr.length);
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
    public CLASSNAMEs2 b(u uVar, long j, long j2) {
        return new CLASSNAMEo2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEo1.a(this, d);
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
