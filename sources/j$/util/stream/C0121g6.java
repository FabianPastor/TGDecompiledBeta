package j$.util.stream;

import java.util.Arrays;
import java.util.Comparator;

/* renamed from: j$.util.stream.g6  reason: case insensitive filesystem */
final class CLASSNAMEg6 extends U5 {
    private Object[] d;
    private int e;

    CLASSNAMEg6(G5 sink, Comparator comparator) {
        super(sink, comparator);
    }

    public void s(long size) {
        if (size < NUM) {
            this.d = new Object[((int) size)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        Arrays.sort(this.d, 0, this.e, this.b);
        this.a.s((long) this.e);
        if (!this.c) {
            for (int i = 0; i < this.e; i++) {
                this.a.accept(this.d[i]);
            }
        } else {
            for (int i2 = 0; i2 < this.e && !this.a.u(); i2++) {
                this.a.accept(this.d[i2]);
            }
        }
        this.a.r();
        this.d = null;
    }

    public void accept(Object t) {
        Object[] objArr = this.d;
        int i = this.e;
        this.e = i + 1;
        objArr[i] = t;
    }
}
