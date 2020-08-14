package j$.util.stream;

import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.k0;
import java.util.Arrays;

/* renamed from: j$.util.stream.o6  reason: case insensitive filesystem */
class CLASSNAMEo6 extends CLASSNAMEq6 implements J {
    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    CLASSNAMEo6() {
    }

    CLASSNAMEo6(int initialCapacity) {
        super(initialCapacity);
    }

    public void forEach(Consumer consumer) {
        if (consumer instanceof J) {
            j((J) consumer);
        } else if (!h7.a) {
            spliterator().forEachRemaining(consumer);
        } else {
            h7.b(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: M */
    public long[][] G(int size) {
        return new long[size][];
    }

    public long[] a(int size) {
        return new long[size];
    }

    /* access modifiers changed from: protected */
    /* renamed from: J */
    public int A(long[] array) {
        return array.length;
    }

    /* access modifiers changed from: protected */
    /* renamed from: I */
    public void z(long[] array, int from, int to, J consumer) {
        for (int i = from; i < to; i++) {
            consumer.accept(array[i]);
        }
    }

    public void accept(long i) {
        H();
        int i2 = this.b;
        this.b = i2 + 1;
        ((long[]) this.e)[i2] = i;
    }

    public long K(long index) {
        int ch = C(index);
        if (this.c == 0 && ch == 0) {
            return ((long[]) this.e)[(int) index];
        }
        return ((long[][]) this.f)[ch][(int) (index - this.d[ch])];
    }

    /* renamed from: L */
    public j$.util.J iterator() {
        return k0.h(spliterator());
    }

    /* renamed from: N */
    public U spliterator() {
        return new CLASSNAMEn6(this, 0, this.c, 0, this.b);
    }

    public String toString() {
        long[] array = (long[]) i();
        if (array.length < 200) {
            return String.format("%s[length=%d, chunks=%d]%s", new Object[]{getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.c), Arrays.toString(array)});
        }
        return String.format("%s[length=%d, chunks=%d]%s...", new Object[]{getClass().getSimpleName(), Integer.valueOf(array.length), Integer.valueOf(this.c), Arrays.toString(Arrays.copyOf(array, 200))});
    }
}
