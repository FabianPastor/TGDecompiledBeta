package j$.util.stream;

import j$.util.H;
import j$.util.S;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.Consumer;
import j$.util.k0;
import java.util.Arrays;

/* renamed from: j$.util.stream.m6  reason: case insensitive filesystem */
class CLASSNAMEm6 extends CLASSNAMEq6 implements B {
    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    CLASSNAMEm6() {
    }

    CLASSNAMEm6(int initialCapacity) {
        super(initialCapacity);
    }

    public void forEach(Consumer consumer) {
        if (consumer instanceof B) {
            j((B) consumer);
        } else if (!h7.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            h7.b(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: M */
    public int[][] G(int size) {
        return new int[size][];
    }

    public int[] a(int size) {
        return new int[size];
    }

    /* access modifiers changed from: protected */
    /* renamed from: J */
    public int A(int[] array) {
        return array.length;
    }

    /* access modifiers changed from: protected */
    /* renamed from: I */
    public void z(int[] array, int from, int to, B consumer) {
        for (int i = from; i < to; i++) {
            consumer.accept(array[i]);
        }
    }

    public void accept(int i) {
        H();
        int i2 = this.b;
        this.b = i2 + 1;
        ((int[]) this.e)[i2] = i;
    }

    public int K(long index) {
        int ch = C(index);
        if (this.c == 0 && ch == 0) {
            return ((int[]) this.e)[(int) index];
        }
        return ((int[][]) this.f)[ch][(int) (index - this.d[ch])];
    }

    /* renamed from: L */
    public H iterator() {
        return k0.g(spliterator());
    }

    /* renamed from: N */
    public S spliterator() {
        return new CLASSNAMEl6(this, 0, this.c, 0, this.b);
    }

    public String toString() {
        int[] array = (int[]) i();
        if (array.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.c), Arrays.toString(array)});
        }
        return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(array, 200))});
    }
}
