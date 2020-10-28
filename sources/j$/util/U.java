package j$.util;

import j$.util.function.Consumer;
import j$.util.function.y;
import java.util.Comparator;

final class U implements E {
    private final long[] a;
    private int b;
    private final int c;
    private final int d;

    public U(long[] jArr, int i, int i2, int i3) {
        this.a = jArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.f(this, consumer);
    }

    public int characteristics() {
        return this.d;
    }

    /* renamed from: d */
    public void forEachRemaining(y yVar) {
        int i;
        yVar.getClass();
        long[] jArr = this.a;
        int length = jArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    yVar.accept(jArr[i]);
                    i++;
                } while (i < i2);
            }
        }
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.c(this, consumer);
    }

    public Comparator getComparator() {
        if (CLASSNAMEk.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }

    /* renamed from: j */
    public boolean tryAdvance(y yVar) {
        yVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        long[] jArr = this.a;
        this.b = i + 1;
        yVar.accept(jArr[i]);
        return true;
    }

    public E trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        long[] jArr = this.a;
        this.b = i2;
        return new U(jArr, i, i2, this.d);
    }
}
