package j$.util.stream;

import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import java.util.Arrays;

class D1 implements A1 {
    final Object[] a;
    int b;

    D1(long j, m mVar) {
        if (j < NUM) {
            this.a = (Object[]) mVar.apply((int) j);
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    D1(Object[] objArr) {
        this.a = objArr;
        this.b = objArr.length;
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.b;
    }

    public void forEach(Consumer consumer) {
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.a[i]);
        }
    }

    public void i(Object[] objArr, int i) {
        System.arraycopy(this.a, 0, objArr, i, this.b);
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public Object[] q(m mVar) {
        Object[] objArr = this.a;
        if (objArr.length == this.b) {
            return objArr;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ A1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.q(this, j, j2, mVar);
    }

    public u spliterator() {
        return L.m(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("ArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
