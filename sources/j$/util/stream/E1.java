package j$.util.stream;

import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.y;
import java.util.Arrays;

class E1 implements B1 {
    final Object[] a;
    int b;

    E1(long j, l lVar) {
        if (j < NUM) {
            this.a = (Object[]) lVar.apply((int) j);
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    E1(Object[] objArr) {
        this.a = objArr;
        this.b = objArr.length;
    }

    public B1 b(int i) {
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

    public Object[] q(l lVar) {
        Object[] objArr = this.a;
        if (objArr.length == this.b) {
            return objArr;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ B1 r(long j, long j2, l lVar) {
        return CLASSNAMEp1.q(this, j, j2, lVar);
    }

    public y spliterator() {
        return N.m(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("ArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
