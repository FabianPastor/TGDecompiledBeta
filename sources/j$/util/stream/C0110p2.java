package j$.util.stream;

import j$.util.function.k;
import j$.util.function.l;
import j$.util.u;

/* renamed from: j$.util.stream.p2  reason: case insensitive filesystem */
final class CLASSNAMEp2 extends CLASSNAMEs2 implements CLASSNAMEk3 {
    private final int[] h;

    CLASSNAMEp2(CLASSNAMEp2 p2Var, u uVar, long j, long j2) {
        super(p2Var, uVar, j, j2, p2Var.h.length);
        this.h = p2Var.h;
    }

    CLASSNAMEp2(u uVar, CLASSNAMEy2 y2Var, int[] iArr) {
        super(uVar, y2Var, iArr.length);
        this.h = iArr;
    }

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

    /* access modifiers changed from: package-private */
    public CLASSNAMEs2 b(u uVar, long j, long j2) {
        return new CLASSNAMEp2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEo1.b(this, num);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }
}
