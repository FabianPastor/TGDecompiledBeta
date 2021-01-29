package j$.util.stream;

import j$.util.stream.S2;
import java.util.Arrays;

final class I2 extends E2 {
    private S2.d c;

    I2(A2 a2) {
        super(a2);
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
                if (this.a.p()) {
                    break;
                }
                this.a.accept(j);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        S2.d dVar;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                dVar = new S2.d();
            }
            this.c = dVar;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
