package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;

/* renamed from: j$.util.stream.h4  reason: case insensitive filesystem */
final class CLASSNAMEh4 extends CLASSNAMEk4 implements f {
    final double[] c;

    CLASSNAMEh4(int i) {
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
        f fVar = (f) obj;
        for (int i = 0; ((long) i) < j; i++) {
            fVar.accept(this.c[i]);
        }
    }

    public f j(f fVar) {
        fVar.getClass();
        return new e(this, fVar);
    }
}
