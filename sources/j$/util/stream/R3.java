package j$.util.stream;

import java.util.Arrays;

final class R3 extends F3 {
    private long[] c;
    private int d;

    R3(CLASSNAMEn3 n3Var) {
        super(n3Var);
    }

    public void accept(long j) {
        long[] jArr = this.c;
        int i = this.d;
        this.d = i + 1;
        jArr[i] = j;
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
            this.c = new long[((int) j)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
