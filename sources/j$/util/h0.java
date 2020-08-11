package j$.util;

import j$.util.function.B;
import j$.util.function.Consumer;
import java.util.Comparator;

final class h0 implements S {
    private final int[] a;
    private int b;
    private final int c;
    private final int d;

    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public h0(int[] array, int origin, int fence, int additionalCharacteristics) {
        this.a = array;
        this.b = origin;
        this.c = fence;
        this.d = additionalCharacteristics | 64 | 16384;
    }

    public S trySplit() {
        int lo = this.b;
        int mid = (this.c + lo) >>> 1;
        if (lo >= mid) {
            return null;
        }
        int[] iArr = this.a;
        this.b = mid;
        return new h0(iArr, lo, mid, this.d);
    }

    /* renamed from: c */
    public void forEachRemaining(B action) {
        if (action != null) {
            int[] iArr = this.a;
            int[] a2 = iArr;
            int length = iArr.length;
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

    /* renamed from: f */
    public boolean tryAdvance(B action) {
        if (action != null) {
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            int[] iArr = this.a;
            this.b = i + 1;
            action.accept(iArr[i]);
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
