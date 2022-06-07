package j$.util.stream;

import j$.util.function.p;
import j$.util.function.q;
import j$.util.u;

/* renamed from: j$.util.stream.q2  reason: case insensitive filesystem */
final class CLASSNAMEq2 extends CLASSNAMEs2 implements CLASSNAMEl3 {
    private final long[] h;

    CLASSNAMEq2(CLASSNAMEq2 q2Var, u uVar, long j, long j2) {
        super(q2Var, uVar, j, j2, q2Var.h.length);
        this.h = q2Var.h;
    }

    CLASSNAMEq2(u uVar, CLASSNAMEy2 y2Var, long[] jArr) {
        super(uVar, y2Var, jArr.length);
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
    public CLASSNAMEs2 b(u uVar, long j, long j2) {
        return new CLASSNAMEq2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEo1.c(this, l);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
