package j$.util.stream;

import java.util.Arrays;

final class H3 extends D3 {
    private V3 c;

    H3(CLASSNAMEn3 n3Var) {
        super(n3Var);
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
        V3 v3;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                v3 = new V3();
            }
            this.c = v3;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
