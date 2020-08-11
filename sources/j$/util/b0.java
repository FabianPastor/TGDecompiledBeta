package j$.util;

import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;
import java.util.Comparator;

final class b0 implements P {
    private final double[] a;
    private int b;
    private final int c;
    private final int d;

    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public b0(double[] array, int origin, int fence, int additionalCharacteristics) {
        this.a = array;
        this.b = origin;
        this.c = fence;
        this.d = additionalCharacteristics | 64 | 16384;
    }

    public P trySplit() {
        int lo = this.b;
        int mid = (this.c + lo) >>> 1;
        if (lo >= mid) {
            return null;
        }
        double[] dArr = this.a;
        this.b = mid;
        return new b0(dArr, lo, mid, this.d);
    }

    /* renamed from: e */
    public void forEachRemaining(CLASSNAMEt action) {
        if (action != null) {
            double[] dArr = this.a;
            double[] a2 = dArr;
            int length = dArr.length;
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

    /* renamed from: j */
    public boolean tryAdvance(CLASSNAMEt action) {
        if (action != null) {
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            double[] dArr = this.a;
            this.b = i + 1;
            action.accept(dArr[i]);
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
