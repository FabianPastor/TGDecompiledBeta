package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.m;
import j$.util.x;
import j$.util.y;
import java.util.Arrays;

class U1 implements CLASSNAMEv1 {
    final double[] a;
    int b;

    U1(long j) {
        if (j < NUM) {
            this.a = new double[((int) j)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    U1(double[] dArr) {
        this.a = dArr;
        this.b = dArr.length;
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.b;
    }

    public void d(Object obj, int i) {
        System.arraycopy(this.a, 0, (double[]) obj, i, this.b);
    }

    public Object e() {
        double[] dArr = this.a;
        int length = dArr.length;
        int i = this.b;
        return length == i ? dArr : Arrays.copyOf(dArr, i);
    }

    /* renamed from: f */
    public /* synthetic */ void i(Double[] dArr, int i) {
        CLASSNAMEp1.h(this, dArr, i);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.k(this, consumer);
    }

    public void g(Object obj) {
        f fVar = (f) obj;
        for (int i = 0; i < this.b; i++) {
            fVar.accept(this.a[i]);
        }
    }

    /* renamed from: k */
    public /* synthetic */ CLASSNAMEv1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.n(this, j, j2, mVar);
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public /* synthetic */ Object[] q(m mVar) {
        return CLASSNAMEp1.g(this, mVar);
    }

    public x spliterator() {
        return N.j(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("DoubleArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m518spliterator() {
        return N.j(this.a, 0, this.b, 1040);
    }
}
