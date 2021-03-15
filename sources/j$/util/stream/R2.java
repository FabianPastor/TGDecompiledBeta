package j$.util.stream;

import java.util.Arrays;
import java.util.Comparator;

final class R2<T> extends F2<T> {
    private Object[] d;
    private int e;

    R2(A2 a2, Comparator comparator) {
        super(a2, comparator);
    }

    public void accept(Object obj) {
        Object[] objArr = this.d;
        int i = this.e;
        this.e = i + 1;
        objArr[i] = obj;
    }

    public void m() {
        int i = 0;
        Arrays.sort(this.d, 0, this.e, this.b);
        this.var_a.n((long) this.e);
        if (!this.c) {
            while (i < this.e) {
                this.var_a.accept(this.d[i]);
                i++;
            }
        } else {
            while (i < this.e && !this.var_a.p()) {
                this.var_a.accept(this.d[i]);
                i++;
            }
        }
        this.var_a.m();
        this.d = null;
    }

    public void n(long j) {
        if (j < NUM) {
            this.d = new Object[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
