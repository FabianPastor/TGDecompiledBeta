package j$.util.stream;

import j$.util.stream.S2;
import java.util.Arrays;

final class G2 extends C2 {
    private S2.b c;

    G2(A2 a2) {
        super(a2);
    }

    public void accept(double d) {
        this.c.accept(d);
    }

    public void l() {
        double[] dArr = (double[]) this.c.e();
        Arrays.sort(dArr);
        this.a.m((long) dArr.length);
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
        this.a.l();
    }

    public void m(long j) {
        S2.b bVar;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                bVar = new S2.b();
            }
            this.c = bVar;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
