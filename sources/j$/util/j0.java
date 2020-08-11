package j$.util;

import j$.util.function.Consumer;
import j$.util.function.J;
import java.util.Comparator;

final class j0 implements U {
    private final long[] a;
    private int b;
    private final int c;
    private final int d;

    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public j0(long[] array, int origin, int fence, int additionalCharacteristics) {
        this.a = array;
        this.b = origin;
        this.c = fence;
        this.d = additionalCharacteristics | 64 | 16384;
    }

    public U trySplit() {
        int lo = this.b;
        int mid = (this.c + lo) >>> 1;
        if (lo >= mid) {
            return null;
        }
        long[] jArr = this.a;
        this.b = mid;
        return new j0(jArr, lo, mid, this.d);
    }

    /* renamed from: d */
    public void forEachRemaining(J action) {
        if (action != null) {
            long[] jArr = this.a;
            long[] a2 = jArr;
            int length = jArr.length;
            int i = this.c;
            int hi = i;
            if (length >= i) {
                int i2 = this.b;
                int i3 = i2;
                if (i2 >= 0) {
                    this.b = hi;
                    if (i3 < hi) {
                        do {
                            action.accept(a2[i3]);
                            i3++;
                        } while (i3 < hi);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        throw null;
    }

    /* renamed from: i */
    public boolean tryAdvance(J action) {
        if (action != null) {
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            long[] jArr = this.a;
            this.b = i + 1;
            action.accept(jArr[i]);
            return true;
        }
        throw null;
    }

    public long estimateSize() {
        return (long) (this.c - this.b);
    }

    public int characteristics() {
        return this.d;
    }

    public Comparator getComparator() {
        if (hasCharacteristics(4)) {
            return null;
        }
        throw new IllegalStateException();
    }
}
