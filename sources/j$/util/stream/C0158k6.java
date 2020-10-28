package j$.util.stream;

import j$.util.function.f;
import j$.util.function.q;

/* renamed from: j$.util.stream.k6  reason: case insensitive filesystem */
final class CLASSNAMEk6 extends n6 implements q {
    final double[] c;

    CLASSNAMEk6(int i) {
        this.c = new double[i];
    }

    public void accept(double d) {
        double[] dArr = this.c;
        int i = this.b;
        this.b = i + 1;
        dArr[i] = d;
    }

    /* access modifiers changed from: package-private */
    public void b(Object obj, long j) {
        q qVar = (q) obj;
        for (int i = 0; ((long) i) < j; i++) {
            qVar.accept(this.c[i]);
        }
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }
}
