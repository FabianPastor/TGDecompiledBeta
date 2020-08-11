package j$.util.stream;

import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;

/* renamed from: j$.util.stream.y6  reason: case insensitive filesystem */
final class CLASSNAMEy6 extends B6 implements CLASSNAMEt {
    final double[] c;

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    CLASSNAMEy6(int size) {
        this.c = new double[size];
    }

    public void accept(double t) {
        double[] dArr = this.c;
        int i = this.b;
        this.b = i + 1;
        dArr[i] = t;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: d */
    public void c(CLASSNAMEt action, long fence) {
        for (int i = 0; ((long) i) < fence; i++) {
            action.accept(this.c[i]);
        }
    }
}
