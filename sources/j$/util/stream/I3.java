package j$.util.stream;

import java.util.Arrays;

final class I3 extends E3 {
    private Y3 c;

    I3(CLASSNAMEm3 m3Var) {
        super(m3Var);
    }

    public void accept(long j) {
        this.c.accept(j);
    }

    public void m() {
        long[] jArr = (long[]) this.c.e();
        Arrays.sort(jArr);
        this.a.n((long) jArr.length);
        int i = 0;
        if (!this.b) {
            int length = jArr.length;
            while (i < length) {
                this.a.accept(jArr[i]);
                i++;
            }
        } else {
            int length2 = jArr.length;
            while (i < length2) {
                long j = jArr[i];
                if (this.a.o()) {
                    break;
                }
                this.a.accept(j);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        Y3 y3;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                y3 = new Y3();
            }
            this.c = y3;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
