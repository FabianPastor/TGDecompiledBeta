package j$.util.stream;

import java.util.Arrays;
import java.util.Comparator;

final class T5 extends H5 {
    private Object[] d;
    private int e;

    T5(CLASSNAMEt5 t5Var, Comparator comparator) {
        super(t5Var, comparator);
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
        this.a.n((long) this.e);
        if (!this.c) {
            while (i < this.e) {
                this.a.accept(this.d[i]);
                i++;
            }
        } else {
            while (i < this.e && !this.a.p()) {
                this.a.accept(this.d[i]);
                i++;
            }
        }
        this.a.m();
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
