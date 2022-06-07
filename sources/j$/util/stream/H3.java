package j$.util.stream;

import java.util.Arrays;

final class H3 extends D3 {
    private W3 c;

    H3(CLASSNAMEm3 m3Var) {
        super(m3Var);
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
                if (this.a.o()) {
                    break;
                }
                this.a.accept(i2);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        W3 w3;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                w3 = new W3();
            }
            this.c = w3;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
