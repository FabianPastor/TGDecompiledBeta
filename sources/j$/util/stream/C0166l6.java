package j$.util.stream;

import j$.util.function.g;
import j$.util.function.u;

/* renamed from: j$.util.stream.l6  reason: case insensitive filesystem */
final class CLASSNAMEl6 extends n6 implements u {
    final int[] c;

    CLASSNAMEl6(int i) {
        this.c = new int[i];
    }

    public void accept(int i) {
        int[] iArr = this.c;
        int i2 = this.b;
        this.b = i2 + 1;
        iArr[i2] = i;
    }

    public void b(Object obj, long j) {
        u uVar = (u) obj;
        for (int i = 0; ((long) i) < j; i++) {
            uVar.accept(this.c[i]);
        }
    }

    public u l(u uVar) {
        uVar.getClass();
        return new g(this, uVar);
    }
}
