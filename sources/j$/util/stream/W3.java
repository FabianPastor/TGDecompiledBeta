package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.g;
import j$.util.function.u;

final class W3 extends Z3 implements CLASSNAMEr5 {
    private final int[] h;

    W3(Spliterator spliterator, CLASSNAMEi4 i4Var, int[] iArr) {
        super(spliterator, i4Var, iArr.length);
        this.h = iArr;
    }

    W3(W3 w3, Spliterator spliterator, long j, long j2) {
        super(w3, spliterator, j, j2, w3.h.length);
        this.h = w3.h;
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
    public Z3 b(Spliterator spliterator, long j, long j2) {
        return new W3(this, spliterator, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Integer num) {
        CLASSNAMEc3.b(this, num);
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
