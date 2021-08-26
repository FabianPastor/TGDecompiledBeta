package j$.util.stream;

import j$.util.y;

/* renamed from: j$.util.stream.s2  reason: case insensitive filesystem */
final class CLASSNAMEs2 extends CLASSNAMEt2 {
    private final Object[] h;

    CLASSNAMEs2(CLASSNAMEs2 s2Var, y yVar, long j, long j2) {
        super(s2Var, yVar, j, j2, s2Var.h.length);
        this.h = s2Var.h;
    }

    CLASSNAMEs2(y yVar, CLASSNAMEz2 z2Var, Object[] objArr) {
        super(yVar, z2Var, objArr.length);
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
    public CLASSNAMEt2 b(y yVar, long j, long j2) {
        return new CLASSNAMEs2(this, yVar, j, j2);
    }
}
