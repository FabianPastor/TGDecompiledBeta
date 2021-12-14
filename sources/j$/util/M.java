package j$.util;

import j$.util.function.Consumer;
import j$.util.function.q;
import java.util.Comparator;

final class M implements w {
    private final long[] a;
    private int b;
    private final int c;
    private final int d;

    public M(long[] jArr, int i, int i2, int i3) {
        this.a = jArr;
        this.b = i;
        this.c = i2;
        this.d = i3 | 64 | 16384;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    public int characteristics() {
        return this.d;
    }

    /* renamed from: d */
    public void forEachRemaining(q qVar) {
        int i;
        qVar.getClass();
        long[] jArr = this.a;
        int length = jArr.length;
        int i2 = this.c;
        if (length >= i2 && (i = this.b) >= 0) {
            this.b = i2;
            if (i < i2) {
                do {
                    qVar.accept(jArr[i]);
                    i++;
                } while (i < i2);
            }
        }
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }

    public Comparator getComparator() {
        if (CLASSNAMEa.f(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    /* renamed from: i */
    public boolean tryAdvance(q qVar) {
        qVar.getClass();
        int i = this.b;
        if (i < 0 || i >= this.c) {
            return false;
        }
        long[] jArr = this.a;
        this.b = i + 1;
        qVar.accept(jArr[i]);
        return true;
    }

    public w trySplit() {
        int i = this.b;
        int i2 = (this.c + i) >>> 1;
        if (i >= i2) {
            return null;
        }
        long[] jArr = this.a;
        this.b = i2;
        return new M(jArr, i, i2, this.d);
    }
}
