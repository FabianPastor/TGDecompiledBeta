package j$.util.stream;

import java.util.Arrays;

final class W5 extends S5 {
    private CLASSNAMEm6 c;

    W5(G5 sink) {
        super(sink);
    }

    public void s(long size) {
        CLASSNAMEm6 m6Var;
        if (size < NUM) {
            if (size > 0) {
                int i = (int) size;
            } else {
                m6Var = new CLASSNAMEm6();
            }
            this.c = m6Var;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        int[] ints = (int[]) this.c.i();
        Arrays.sort(ints);
        this.a.s((long) ints.length);
        int i = 0;
        if (!this.b) {
            int length = ints.length;
            while (i < length) {
                this.a.accept(ints[i]);
                i++;
            }
        } else {
            int length2 = ints.length;
            while (i < length2) {
                int anInt = ints[i];
                if (this.a.u()) {
                    break;
                }
                this.a.accept(anInt);
                i++;
            }
        }
        this.a.r();
    }

    public void accept(int t) {
        this.c.accept(t);
    }
}
