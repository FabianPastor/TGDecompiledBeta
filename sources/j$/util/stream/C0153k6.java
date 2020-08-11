package j$.util.stream;

import j$.util.F;
import j$.util.P;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import j$.util.k0;
import java.util.Arrays;

/* renamed from: j$.util.stream.k6  reason: case insensitive filesystem */
class CLASSNAMEk6 extends CLASSNAMEq6 implements CLASSNAMEt {
    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    CLASSNAMEk6() {
    }

    CLASSNAMEk6(int initialCapacity) {
        super(initialCapacity);
    }

    public void forEach(Consumer consumer) {
        if (consumer instanceof CLASSNAMEt) {
            j((CLASSNAMEt) consumer);
        } else if (!h7.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            h7.b(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: M */
    public double[][] G(int size) {
        return new double[size][];
    }

    public double[] a(int size) {
        return new double[size];
    }

    /* access modifiers changed from: protected */
    /* renamed from: J */
    public int A(double[] array) {
        return array.length;
    }

    /* access modifiers changed from: protected */
    /* renamed from: I */
    public void z(double[] array, int from, int to, CLASSNAMEt consumer) {
        for (int i = from; i < to; i++) {
            consumer.accept(array[i]);
        }
    }

    public void accept(double i) {
        H();
        int i2 = this.b;
        this.b = i2 + 1;
        ((double[]) this.e)[i2] = i;
    }

    public double K(long index) {
        int ch = C(index);
        if (this.c == 0 && ch == 0) {
            return ((double[]) this.e)[(int) index];
        }
        return ((double[][]) this.f)[ch][(int) (index - this.d[ch])];
    }

    /* renamed from: L */
    public F iterator() {
        return k0.f(spliterator());
    }

    /* renamed from: N */
    public P spliterator() {
        return new CLASSNAMEj6(this, 0, this.c, 0, this.b);
    }

    public String toString() {
        double[] array = (double[]) i();
        if (array.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.c), Arrays.toString(array)});
        }
        return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(array, 200))});
    }
}
