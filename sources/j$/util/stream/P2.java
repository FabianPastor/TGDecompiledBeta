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

    public void l() {
        int i = 0;
        Arrays.sort(this.c, 0, this.d);
        this.a.m((long) this.d);
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
        this.a.l();
        this.c = null;
    }

    public void m(long j) {
        if (j < NUM) {
            this.c = new int[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
