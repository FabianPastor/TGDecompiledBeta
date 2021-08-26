package j$.util.stream;

import j$.util.function.j;
import j$.util.function.k;
import j$.util.y;

/* renamed from: j$.util.stream.q2  reason: case insensitive filesystem */
final class CLASSNAMEq2 extends CLASSNAMEt2 implements CLASSNAMEl3 {
    private final int[] h;

    CLASSNAMEq2(CLASSNAMEq2 q2Var, y yVar, long j, long j2) {
        super(q2Var, yVar, j, j2, q2Var.h.length);
        this.h = q2Var.h;
    }

    CLASSNAMEq2(y yVar, CLASSNAMEz2 z2Var, int[] iArr) {
        super(yVar, z2Var, iArr.length);
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
    public CLASSNAMEt2 b(y yVar, long j, long j2) {
        return new CLASSNAMEq2(this, yVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEp1.b(this, num);
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }
}
