package j$.util.stream;

import java.util.Arrays;

final class J3 extends F3 {
    private Z3 c;

    J3(CLASSNAMEn3 n3Var) {
        super(n3Var);
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
                if (this.a.o()) {
                    break;
                }
                this.a.accept(j);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        Z3 z3;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                z3 = new Z3();
            }
            this.c = z3;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
