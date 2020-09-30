package j$.util.stream;

import java.util.Arrays;

/* renamed from: j$.util.stream.e6  reason: case insensitive filesystem */
final class CLASSNAMEe6 extends S5 {
    private int[] c;
    private int d;

    CLASSNAMEe6(G5 downstream) {
        super(downstream);
    }

    public void s(long size) {
        if (size < NUM) {
            this.c = new int[((int) size)];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public void r() {
        Arrays.sort(this.c, 0, this.d);
        this.a.s((long) this.d);
        if (!this.b) {
            for (int i = 0; i < this.d; i++) {
                this.a.accept(this.c[i]);
            }
        } else {
            for (int i2 = 0; i2 < this.d && !this.a.u(); i2++) {
                this.a.accept(this.c[i2]);
            }
        }
        this.a.r();
        this.c = null;
    }

    public void accept(int t) {
        int[] iArr = this.c;
        int i = this.d;
        this.d = i + 1;
        iArr[i] = t;
    }
}
