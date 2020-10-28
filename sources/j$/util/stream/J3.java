package j$.util.stream;

import j$.util.F;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.u;
import j$.util.function.v;
import java.util.Arrays;

class J3 implements CLASSNAMEi3 {
    final int[] a;
    int b;

    J3(long j) {
        if (j < NUM) {
            this.a = new int[((int) j)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    J3(int[] iArr) {
        this.a = iArr;
        this.b = iArr.length;
    }

    public CLASSNAMEk3 b(int i) {
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

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEc3.i(this, consumer);
    }

    /* renamed from: g */
    public /* synthetic */ void j(Integer[] numArr, int i) {
        CLASSNAMEc3.f(this, numArr, i);
    }

    public void h(Object obj) {
        u uVar = (u) obj;
        for (int i = 0; i < this.b; i++) {
            uVar.accept(this.a[i]);
        }
    }

    /* renamed from: k */
    public /* synthetic */ CLASSNAMEi3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.l(this, j, j2, vVar);
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public /* synthetic */ Object[] q(v vVar) {
        return CLASSNAMEc3.d(this, vVar);
    }

    public F spliterator() {
        return V.k(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("IntArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }

    /* renamed from: spliterator  reason: collision with other method in class */
    public Spliterator m15spliterator() {
        return V.k(this.a, 0, this.b, 1040);
    }
}
