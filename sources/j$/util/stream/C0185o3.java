package j$.util.stream;

import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.v;
import java.util.Arrays;

/* renamed from: j$.util.stream.o3  reason: case insensitive filesystem */
class CLASSNAMEo3 implements CLASSNAMEl3 {
    final Object[] a;
    int b;

    CLASSNAMEo3(long j, v vVar) {
        if (j < NUM) {
            this.a = (Object[]) vVar.apply((int) j);
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    CLASSNAMEo3(Object[] objArr) {
        this.a = objArr;
        this.b = objArr.length;
    }

    public CLASSNAMEl3 b(int i) {
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

    public void j(Object[] objArr, int i) {
        System.arraycopy(this.a, 0, objArr, i, this.b);
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public Object[] q(v vVar) {
        Object[] objArr = this.a;
        if (objArr.length == this.b) {
            return objArr;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ CLASSNAMEl3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.n(this, j, j2, vVar);
    }

    public Spliterator spliterator() {
        return V.m(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("ArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
