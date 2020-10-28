package j$.util.stream;

import java.util.Arrays;

final class J5 extends F5 {
    private Y5 c;

    J5(CLASSNAMEt5 t5Var) {
        super(t5Var);
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
        Y5 y5;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                y5 = new Y5();
            }
            this.c = y5;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
