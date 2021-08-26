package j$.util.stream;

import j$.util.function.o;
import j$.util.function.p;
import j$.util.y;

/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
final class CLASSNAMEr2 extends CLASSNAMEt2 implements CLASSNAMEm3 {
    private final long[] h;

    CLASSNAMEr2(CLASSNAMEr2 r2Var, y yVar, long j, long j2) {
        super(r2Var, yVar, j, j2, r2Var.h.length);
        this.h = r2Var.h;
    }

    CLASSNAMEr2(y yVar, CLASSNAMEz2 z2Var, long[] jArr) {
        super(yVar, z2Var, jArr.length);
        this.h = jArr;
    }

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

    /* access modifiers changed from: package-private */
    public CLASSNAMEt2 b(y yVar, long j, long j2) {
        return new CLASSNAMEr2(this, yVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }
}
