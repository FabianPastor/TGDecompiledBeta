package j$.util.stream;

import java.util.Arrays;

final class Q3 extends E3 {
    private int[] c;
    private int d;

    Q3(CLASSNAMEn3 n3Var) {
        super(n3Var);
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
            this.c = new int[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
