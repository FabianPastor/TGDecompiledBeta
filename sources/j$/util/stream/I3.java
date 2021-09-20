package j$.util.stream;

import java.util.Arrays;

final class I3 extends E3 {
    private X3 c;

    I3(CLASSNAMEn3 n3Var) {
        super(n3Var);
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
        X3 x3;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                x3 = new X3();
            }
            this.c = x3;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
