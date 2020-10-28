package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.v;
import j$.util.function.y;
import java.util.Arrays;

class S3 implements CLASSNAMEj3 {
    final long[] a;
    int b;

    S3(long j) {
        if (j < NUM) {
            this.a = new long[((int) j)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    S3(long[] jArr) {
        this.a = jArr;
        this.b = jArr.length;
    }

    public CLASSNAMEk3 b(int i) {
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
        CLASSNAMEc3.j(this, consumer);
    }

    public void h(Object obj) {
        y yVar = (y) obj;
        for (int i = 0; i < this.b; i++) {
            yVar.accept(this.a[i]);
        }
    }

    /* renamed from: k */
    public /* synthetic */ void j(Long[] lArr, int i) {
        CLASSNAMEc3.g(this, lArr, i);
    }

    /* renamed from: l */
    public /* synthetic */ CLASSNAMEj3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.m(this, j, j2, vVar);
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public /* synthetic */ Object[] q(v vVar) {
        return CLASSNAMEc3.d(this, vVar);
    }

    public F spliterator() {
        return V.l(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("LongArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m20spliterator() {
        return V.l(this.a, 0, this.b, 1040);
    }
}
