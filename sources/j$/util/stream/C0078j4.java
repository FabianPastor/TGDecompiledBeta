package j$.util.stream;

import j$.util.function.o;
import j$.util.function.p;

/* renamed from: j$.util.stream.j4  reason: case insensitive filesystem */
final class CLASSNAMEj4 extends CLASSNAMEk4 implements p {
    final long[] c;

    CLASSNAMEj4(int i) {
        this.c = new long[i];
    }

    public void accept(long j) {
        long[] jArr = this.c;
        int i = this.b;
        this.b = i + 1;
        jArr[i] = j;
    }

    public void b(Object obj, long j) {
        p pVar = (p) obj;
        for (int i = 0; ((long) i) < j; i++) {
            pVar.accept(this.c[i]);
        }
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }
}
