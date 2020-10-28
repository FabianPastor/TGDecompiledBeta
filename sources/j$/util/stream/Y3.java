package j$.util.stream;

import j$.util.Spliterator;

final class Y3 extends Z3 implements CLASSNAMEt5 {
    private final Object[] h;

    Y3(Spliterator spliterator, CLASSNAMEi4 i4Var, Object[] objArr) {
        super(spliterator, i4Var, objArr.length);
        this.h = objArr;
    }

    Y3(Y3 y3, Spliterator spliterator, long j, long j2) {
        super(y3, spliterator, j, j2, y3.h.length);
        this.h = y3.h;
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
    public Z3 b(Spliterator spliterator, long j, long j2) {
        return new Y3(this, spliterator, j, j2);
    }
}
