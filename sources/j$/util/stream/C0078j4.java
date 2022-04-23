package j$.util.stream;

import j$.util.function.p;
import j$.util.function.q;

/* renamed from: j$.util.stream.j4  reason: case insensitive filesystem */
final class CLASSNAMEj4 extends CLASSNAMEk4 implements q {
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
        q qVar = (q) obj;
        for (int i = 0; ((long) i) < j; i++) {
            qVar.accept(this.c[i]);
        }
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
