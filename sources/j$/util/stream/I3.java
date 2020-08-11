package j$.util.stream;

import j$.util.CLASSNAMEn;
import j$.util.P;
import j$.util.function.C;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import java.util.Arrays;

class I3 implements CLASSNAMEm3 {
    final double[] a;
    int b;

    public /* synthetic */ void e(Double[] dArr, int i) {
        CLASSNAMEl3.a(this, dArr, i);
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        CLASSNAMEl3.c(this, consumer);
    }

    public /* bridge */ /* synthetic */ void m(Object[] objArr, int i) {
        e((Double[]) objArr, i);
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    public /* synthetic */ Object[] x(C c) {
        return CLASSNAMEr3.a(this, c);
    }

    I3(long size) {
        if (size < NUM) {
            this.a = new double[((int) size)];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    I3(double[] array) {
        this.a = array;
        this.b = array.length;
    }

    /* renamed from: z */
    public P spliterator() {
        return CLASSNAMEn.a(this.a, 0, this.b);
    }

    /* renamed from: h */
    public double[] i() {
        double[] dArr = this.a;
        int length = dArr.length;
        int i = this.b;
        if (length == i) {
            return dArr;
        }
        return Arrays.copyOf(dArr, i);
    }

    /* renamed from: q */
    public void f(double[] dest, int destOffset) {
        System.arraycopy(this.a, 0, dest, destOffset, this.b);
    }

    public long count() {
        return (long) this.b;
    }

    /* renamed from: y */
    public void j(CLASSNAMEt consumer) {
        for (int i = 0; i < this.b; i++) {
            consumer.accept(this.a[i]);
        }
    }

    public String toString() {
        return String.format("DoubleArrayNode[%d][%s]", new Object[]{Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a)});
    }
}
