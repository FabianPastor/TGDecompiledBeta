package j$.util.stream;

import java.util.Arrays;

final class V5 extends R5 {
    private CLASSNAMEk6 c;

    V5(G5 sink) {
        super(sink);
    }

    public void s(long size) {
        CLASSNAMEk6 k6Var;
        if (size < NUM) {
            if (size > 0) {
                int i = (int) size;
            } else {
                k6Var = new CLASSNAMEk6();
            }
            this.c = k6Var;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        double[] doubles = (double[]) this.c.i();
        Arrays.sort(doubles);
        this.a.s((long) doubles.length);
        int i = 0;
        if (!this.b) {
            int length = doubles.length;
            while (i < length) {
                this.a.accept(doubles[i]);
                i++;
            }
        } else {
            int length2 = doubles.length;
            while (i < length2) {
                double aDouble = doubles[i];
                if (this.a.u()) {
                    break;
                }
                this.a.accept(aDouble);
                i++;
            }
        }
        this.a.r();
    }

    public void accept(double t) {
        this.c.accept(t);
    }
}
