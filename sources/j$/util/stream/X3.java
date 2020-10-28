package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.h;
import j$.util.function.y;

final class X3 extends Z3 implements CLASSNAMEs5 {
    private final long[] h;

    X3(Spliterator spliterator, CLASSNAMEi4 i4Var, long[] jArr) {
        super(spliterator, i4Var, jArr.length);
        this.h = jArr;
    }

    X3(X3 x3, Spliterator spliterator, long j, long j2) {
        super(x3, spliterator, j, j2, x3.h.length);
        this.h = x3.h;
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
    public Z3 b(Spliterator spliterator, long j, long j2) {
        return new X3(this, spliterator, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEc3.c(this, l);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }
}
