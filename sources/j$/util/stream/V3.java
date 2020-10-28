package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.f;
import j$.util.function.q;

final class V3 extends Z3 implements CLASSNAMEq5 {
    private final double[] h;

    V3(Spliterator spliterator, CLASSNAMEi4 i4Var, double[] dArr) {
        super(spliterator, i4Var, dArr.length);
        this.h = dArr;
    }

    V3(V3 v3, Spliterator spliterator, long j, long j2) {
        super(v3, spliterator, j, j2, v3.h.length);
        this.h = v3.h;
    }

    public void accept(double d) {
        int i = this.f;
        if (i < this.g) {
            double[] dArr = this.h;
            this.f = i + 1;
            dArr[i] = d;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    /* access modifiers changed from: package-private */
    public Z3 b(Spliterator spliterator, long j, long j2) {
        return new V3(this, spliterator, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Double d) {
        CLASSNAMEc3.a(this, d);
    }

    public q k(q qVar) {
        qVar.getClass();
        return new f(this, qVar);
    }
}
