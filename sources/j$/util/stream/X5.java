package j$.util.stream;

import java.util.Arrays;

final class X5 extends T5 {
    private CLASSNAMEo6 c;

    X5(G5 sink) {
        super(sink);
    }

    public void s(long size) {
        CLASSNAMEo6 o6Var;
        if (size < NUM) {
            if (size > 0) {
                int i = (int) size;
            } else {
                o6Var = new CLASSNAMEo6();
            }
            this.c = o6Var;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        long[] longs = (long[]) this.c.i();
        Arrays.sort(longs);
        this.a.s((long) longs.length);
        int i = 0;
        if (!this.b) {
            int length = longs.length;
            while (i < length) {
                this.a.accept(longs[i]);
                i++;
            }
        } else {
            int length2 = longs.length;
            while (i < length2) {
                long aLong = longs[i];
                if (this.a.u()) {
                    break;
                }
                this.a.accept(aLong);
                i++;
            }
        }
        this.a.r();
    }

    public void accept(long t) {
        this.c.accept(t);
    }
}
