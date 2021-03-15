package j$.util.stream;

import java.util.Arrays;

final class P2 extends D2 {
    private int[] c;
    private int d;

    P2(A2 a2) {
        super(a2);
    }

    public void accept(int i) {
        int[] iArr = this.c;
        int i2 = this.d;
        this.d = i2 + 1;
        iArr[i2] = i;
    }

    public void m() {
        int i = 0;
        Arrays.sort(this.c, 0, this.d);
        this.var_a.n((long) this.d);
        if (!this.b) {
            while (i < this.d) {
                this.var_a.accept(this.c[i]);
                i++;
            }
        } else {
            while (i < this.d && !this.var_a.p()) {
                this.var_a.accept(this.c[i]);
                i++;
            }
        }
        this.var_a.m();
        this.c = null;
    }

    public void n(long j) {
        if (j < NUM) {
            this.c = new int[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
