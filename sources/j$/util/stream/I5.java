package j$.util.stream;

import java.util.Arrays;

final class I5 extends E5 {
    private W5 c;

    I5(CLASSNAMEt5 t5Var) {
        super(t5Var);
    }

    public void accept(double d) {
        this.c.accept(d);
    }

    public void m() {
        double[] dArr = (double[]) this.c.e();
        Arrays.sort(dArr);
        this.a.n((long) dArr.length);
        int i = 0;
        if (!this.b) {
            int length = dArr.length;
            while (i < length) {
                this.a.accept(dArr[i]);
                i++;
            }
        } else {
            int length2 = dArr.length;
            while (i < length2) {
                double d = dArr[i];
                if (this.a.p()) {
                    break;
                }
                this.a.accept(d);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        W5 w5;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                w5 = new W5();
            }
            this.c = w5;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
