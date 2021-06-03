package j$.util.stream;

import java.util.Arrays;

final class Q2 extends E2 {
    private long[] c;
    private int d;

    Q2(A2 a2) {
        super(a2);
    }

    public void accept(long j) {
        long[] jArr = this.c;
        int i = this.d;
        this.d = i + 1;
        jArr[i] = j;
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
            this.c = new long[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
