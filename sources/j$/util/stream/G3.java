package j$.util.stream;

import java.util.Arrays;

final class G3 extends C3 {
    private U3 c;

    G3(CLASSNAMEm3 m3Var) {
        super(m3Var);
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
                if (this.a.o()) {
                    break;
                }
                this.a.accept(d);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        U3 u3;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                u3 = new U3();
            }
            this.c = u3;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
