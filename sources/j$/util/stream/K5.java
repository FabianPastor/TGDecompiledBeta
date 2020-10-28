package j$.util.stream;

import java.util.Arrays;

final class K5 extends G5 {
    private CLASSNAMEa6 c;

    K5(CLASSNAMEt5 t5Var) {
        super(t5Var);
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
                if (this.a.p()) {
                    break;
                }
                this.a.accept(j);
                i++;
            }
        }
        this.a.m();
    }

    public void n(long j) {
        CLASSNAMEa6 a6Var;
        if (j < NUM) {
            if (j > 0) {
                int i = (int) j;
            } else {
                a6Var = new CLASSNAMEa6();
            }
            this.c = a6Var;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
