package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.S;
import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.Consumer;
import java.util.Arrays;

class R3 implements CLASSNAMEo3 {
    final int[] a;
    int b;

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEn3.c(this, consumer);
    }

    public /* synthetic */ void k(Integer[] numArr, int i) {
        CLASSNAMEn3.a(this, numArr, i);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        k((Integer[]) objArr, i);
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    public /* synthetic */ Object[] x(C c) {
        return CLASSNAMEr3.a(this, c);
    }

    R3(long size) {
        if (size < NUM) {
            this.a = new int[((int) size)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    R3(int[] array) {
        this.a = array;
        this.b = array.length;
    }

    /* renamed from: z */
    public S spliterator() {
        return CLASSNAMEn.b(this.a, 0, this.b);
    }

    /* renamed from: h */
    public int[] i() {
        int[] iArr = this.a;
        int length = iArr.length;
        int i = this.b;
        if (length == i) {
            return iArr;
        }
        return Arrays.copyOf(iArr, i);
    }

    /* renamed from: p */
    public void f(int[] dest, int destOffset) {
        System.arraycopy(this.a, 0, dest, destOffset, this.b);
    }

    public long count() {
        return (long) this.b;
    }

    /* renamed from: y */
    public void j(B consumer) {
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.a[i]);
        }
    }

    public String toString() {
        return String.format("IntArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
