package j$.util.stream;

import j$.util.function.k;
import j$.util.function.l;

/* renamed from: j$.util.stream.i4  reason: case insensitive filesystem */
final class CLASSNAMEi4 extends CLASSNAMEk4 implements l {
    final int[] c;

    CLASSNAMEi4(int i) {
        this.c = new int[i];
    }

    public void accept(int i) {
        int[] iArr = this.c;
        int i2 = this.b;
        this.b = i2 + 1;
        iArr[i2] = i;
    }

    public void b(Object obj, long j) {
        l lVar = (l) obj;
        for (int i = 0; ((long) i) < j; i++) {
            lVar.accept(this.c[i]);
        }
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }
}
