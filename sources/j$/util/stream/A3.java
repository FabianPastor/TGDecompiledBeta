package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.v;
import java.util.Arrays;

class A3 implements CLASSNAMEh3 {
    final double[] a;
    int b;

    A3(long j) {
        if (j < NUM) {
            this.a = new double[((int) j)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    A3(double[] dArr) {
        this.a = dArr;
        this.b = dArr.length;
    }

    public CLASSNAMEk3 b(int i) {
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

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.h(this, consumer);
    }

    /* renamed from: g */
    public /* synthetic */ void j(Double[] dArr, int i) {
        CLASSNAMEc3.e(this, dArr, i);
    }

    public void h(Object obj) {
        q qVar = (q) obj;
        for (int i = 0; i < this.b; i++) {
            qVar.accept(this.a[i]);
        }
    }

    /* renamed from: l */
    public /* synthetic */ CLASSNAMEh3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.k(this, j, j2, vVar);
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public /* synthetic */ Object[] q(v vVar) {
        return CLASSNAMEc3.d(this, vVar);
    }

    public F spliterator() {
        return V.j(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("DoubleArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m9spliterator() {
        return V.j(this.a, 0, this.b, 1040);
    }
}
