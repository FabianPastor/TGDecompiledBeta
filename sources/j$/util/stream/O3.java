package j$.util.stream;

import java.util.Arrays;

final class O3 extends C3 {
    private double[] c;
    private int d;

    O3(CLASSNAMEm3 m3Var) {
        super(m3Var);
    }

    public void accept(double d2) {
        double[] dArr = this.c;
        int i = this.d;
        this.d = i + 1;
        dArr[i] = d2;
    }

    public void m() {
        int i = 0;
        Arrays.sort(this.c, 0, this.d);
        this.a.n((long) this.d);
        if (!this.b) {
            while (i < this.d) {
                this.a.accept(this.c[i]);
                i++;
            }
        } else {
            while (i < this.d && !this.a.o()) {
                this.a.accept(this.c[i]);
                i++;
            }
        }
        this.a.m();
        this.c = null;
    }

    public void n(long j) {
        if (j < NUM) {
            this.c = new double[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
