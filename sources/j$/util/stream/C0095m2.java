package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.function.q;
import j$.util.x;
import j$.util.y;
import java.util.Arrays;

/* renamed from: j$.util.stream.m2  reason: case insensitive filesystem */
class CLASSNAMEm2 implements CLASSNAMEz1 {
    final long[] a;
    int b;

    CLASSNAMEm2(long j) {
        if (j < NUM) {
            this.a = new long[((int) j)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    CLASSNAMEm2(long[] jArr) {
        this.a = jArr;
        this.b = jArr.length;
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.b;
    }

    public void d(Object obj, int i) {
        System.arraycopy(this.a, 0, (long[]) obj, i, this.b);
    }

    public Object e() {
        long[] jArr = this.a;
        int length = jArr.length;
        int i = this.b;
        return length == i ? jArr : Arrays.copyOf(jArr, i);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.m(this, consumer);
    }

    public void g(Object obj) {
        q qVar = (q) obj;
        for (int i = 0; i < this.b; i++) {
            qVar.accept(this.a[i]);
        }
    }

    /* renamed from: j */
    public /* synthetic */ void i(Long[] lArr, int i) {
        CLASSNAMEp1.j(this, lArr, i);
    }

    /* renamed from: k */
    public /* synthetic */ CLASSNAMEz1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.p(this, j, j2, mVar);
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public /* synthetic */ Object[] q(m mVar) {
        return CLASSNAMEp1.g(this, mVar);
    }

    public x spliterator() {
        return N.l(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("LongArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m561spliterator() {
        return N.l(this.a, 0, this.b, 1040);
    }
}
