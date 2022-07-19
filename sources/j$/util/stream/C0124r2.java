package j$.util.stream;

import j$.util.u;

/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
final class CLASSNAMEr2 extends CLASSNAMEs2 {
    private final Object[] h;

    CLASSNAMEr2(CLASSNAMEr2 r2Var, u uVar, long j, long j2) {
        super(r2Var, uVar, j, j2, r2Var.h.length);
        this.h = r2Var.h;
    }

    CLASSNAMEr2(u uVar, CLASSNAMEy2 y2Var, Object[] objArr) {
        super(uVar, y2Var, objArr.length);
        this.h = objArr;
    }

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

    /* access modifiers changed from: package-private */
    public CLASSNAMEs2 b(u uVar, long j, long j2) {
        return new CLASSNAMEr2(this, uVar, j, j2);
    }
}
