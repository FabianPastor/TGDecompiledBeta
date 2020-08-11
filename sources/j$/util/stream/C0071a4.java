package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.U;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.J;
import java.util.Arrays;

/* renamed from: j$.util.stream.a4  reason: case insensitive filesystem */
class CLASSNAMEa4 implements CLASSNAMEq3 {
    final long[] a;
    int b;

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEp3.c(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        o((Long[]) objArr, i);
    }

    public /* synthetic */ void o(Long[] lArr, int i) {
        CLASSNAMEp3.a(this, lArr, i);
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    public /* synthetic */ Object[] x(C c) {
        return CLASSNAMEr3.a(this, c);
    }

    CLASSNAMEa4(long size) {
        if (size < NUM) {
            this.a = new long[((int) size)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    CLASSNAMEa4(long[] array) {
        this.a = array;
        this.b = array.length;
    }

    /* renamed from: z */
    public U spliterator() {
        return CLASSNAMEn.c(this.a, 0, this.b);
    }

    /* renamed from: p */
    public long[] i() {
        long[] jArr = this.a;
        int length = jArr.length;
        int i = this.b;
        if (length == i) {
            return jArr;
        }
        return Arrays.copyOf(jArr, i);
    }

    /* renamed from: q */
    public void f(long[] dest, int destOffset) {
        System.arraycopy(this.a, 0, dest, destOffset, this.b);
    }

    public long count() {
        return (long) this.b;
    }

    /* renamed from: y */
    public void j(J consumer) {
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.a[i]);
        }
    }

    public String toString() {
        return String.format("LongArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
