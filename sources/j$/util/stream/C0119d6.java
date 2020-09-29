package j$.util.stream;

import java.util.Arrays;

/* renamed from: j$.util.stream.d6  reason: case insensitive filesystem */
final class CLASSNAMEd6 extends R5 {
    private double[] c;
    private int d;

    CLASSNAMEd6(G5 downstream) {
        super(downstream);
    }

    public void s(long size) {
        if (size < NUM) {
            this.c = new double[((int) size)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        Arrays.sort(this.c, 0, this.d);
        this.a.s((long) this.d);
        if (!this.b) {
            for (int i = 0; i < this.d; i++) {
                this.a.accept(this.c[i]);
            }
        } else {
            for (int i2 = 0; i2 < this.d && !this.a.u(); i2++) {
                this.a.accept(this.c[i2]);
            }
        }
        this.a.r();
        this.c = null;
    }

    public void accept(double t) {
        double[] dArr = this.c;
        int i = this.d;
        this.d = i + 1;
        dArr[i] = t;
    }
}
