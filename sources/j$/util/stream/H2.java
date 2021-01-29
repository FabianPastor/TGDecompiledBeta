package j$.util.stream;

import j$.util.stream.S2;
import java.util.Arrays;

final class H2 extends D2 {
    private S2.c c;

    H2(A2 a2) {
        super(a2);
    }

    public void accept(int i) {
        this.c.accept(i);
    }

    public void m() {
        int[] iArr = (int[]) this.c.e();
        Arrays.sort(iArr);
        this.a.n((long) iArr.length);
        int i = 0;
        if (!this.b) {
            int length = iArr.length;
            while (i < length) {
                this.a.accept(iArr[i]);
                i++;
            }
        } else {
            int length2 = iArr.length;
            while (i < length2) {
                int i2 = iArr[i];
                if (this.a.p()) {
                    break;
                }
                this.a.accept(i2);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        S2.c cVar;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                cVar = new S2.c();
            }
            this.c = cVar;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
