package j$.util.stream;

import j$.util.function.j;
import j$.util.function.k;

/* renamed from: j$.util.stream.i4  reason: case insensitive filesystem */
final class CLASSNAMEi4 extends CLASSNAMEk4 implements k {
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
        k kVar = (k) obj;
        for (int i = 0; ((long) i) < j; i++) {
            kVar.accept(this.c[i]);
        }
    }

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }
}
