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

    public void l() {
        int i = 0;
        Arrays.sort(this.d, 0, this.e, this.b);
        this.a.m((long) this.e);
        if (!this.c) {
            while (i < this.e) {
                this.a.accept(this.d[i]);
                i++;
            }
        } else {
            while (i < this.e && !this.a.o()) {
                this.a.accept(this.d[i]);
                i++;
            }
        }
        this.a.l();
        this.d = null;
    }

    public void m(long j) {
        if (j < NUM) {
            this.d = new Object[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
