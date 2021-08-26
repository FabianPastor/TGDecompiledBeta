package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.x;
import j$.util.y;
import java.util.Arrays;

/* renamed from: j$.util.stream.d2  reason: case insensitive filesystem */
class CLASSNAMEd2 implements CLASSNAMEx1 {
    final int[] a;
    int b;

    CLASSNAMEd2(long j) {
        if (j < NUM) {
            this.a = new int[((int) j)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    CLASSNAMEd2(int[] iArr) {
        this.a = iArr;
        this.b = iArr.length;
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.b;
    }

    public void d(Object obj, int i) {
        System.arraycopy(this.a, 0, (int[]) obj, i, this.b);
    }

    public Object e() {
        int[] iArr = this.a;
        int length = iArr.length;
        int i = this.b;
        return length == i ? iArr : Arrays.copyOf(iArr, i);
    }

    /* renamed from: f */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        CLASSNAMEp1.i(this, numArr, i);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp1.l(this, consumer);
    }

    public void g(Object obj) {
        k kVar = (k) obj;
        for (int i = 0; i < this.b; i++) {
            kVar.accept(this.a[i]);
        }
    }

    /* renamed from: j */
    public /* synthetic */ CLASSNAMEx1 r(long j, long j2, l lVar) {
        return CLASSNAMEp1.o(this, j, j2, lVar);
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public /* synthetic */ Object[] q(l lVar) {
        return CLASSNAMEp1.g(this, lVar);
    }

    public x spliterator() {
        return N.k(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("IntArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public y m11spliterator() {
        return N.k(this.a, 0, this.b, 1040);
    }
}
