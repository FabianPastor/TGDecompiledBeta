package j$.util.stream;

import j$.util.function.h;
import j$.util.function.y;

final class m6 extends n6 implements y {
    final long[] c;

    m6(int i) {
        this.c = new long[i];
    }

    public void accept(long j) {
        long[] jArr = this.c;
        int i = this.b;
        this.b = i + 1;
        jArr[i] = j;
    }

    public void b(Object obj, long j) {
        y yVar = (y) obj;
        for (int i = 0; ((long) i) < j; i++) {
            yVar.accept(this.c[i]);
        }
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }
}
