package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import java.util.Arrays;

/* renamed from: j$.util.stream.w3  reason: case insensitive filesystem */
class CLASSNAMEw3 implements CLASSNAMEt3 {
    final Object[] a;
    int b;

    public /* synthetic */ CLASSNAMEt3 c(long j, long j2, C c) {
        return CLASSNAMEg3.d(this, j, j2, c);
    }

    public /* synthetic */ CLASSNAMEt3 d(int i) {
        CLASSNAMEg3.a(this);
        throw null;
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    CLASSNAMEw3(long size, C c) {
        if (size < NUM) {
            this.a = (Object[]) c.a((int) size);
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    CLASSNAMEw3(Object[] array) {
        this.a = array;
        this.b = array.length;
    }

    public Spliterator spliterator() {
        return CLASSNAMEn.d(this.a, 0, this.b);
    }

    public void m(Object[] dest, int destOffset) {
        System.arraycopy(this.a, 0, dest, destOffset, this.b);
    }

    public Object[] x(C c) {
        Object[] objArr = this.a;
        if (objArr.length == this.b) {
            return objArr;
        }
        throw new IllegalStateException();
    }

    public long count() {
        return (long) this.b;
    }

    public void forEach(Consumer consumer) {
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.a[i]);
        }
    }

    public String toString() {
        return String.format("ArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
