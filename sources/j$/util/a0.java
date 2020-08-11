package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;

final class a0 implements Spliterator {
    private final Object[] a;
    private int b;
    private final int c;
    private final int d;

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    public a0(Object[] array, int origin, int fence, int additionalCharacteristics) {
        this.a = array;
        this.b = origin;
        this.c = fence;
        this.d = additionalCharacteristics | 64 | 16384;
    }

    public Spliterator trySplit() {
        int lo = this.b;
        int mid = (this.c + lo) >>> 1;
        if (lo >= mid) {
            return null;
        }
        Object[] objArr = this.a;
        this.b = mid;
        return new a0(objArr, lo, mid, this.d);
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer != null) {
            Object[] objArr = this.a;
            Object[] a2 = objArr;
            int length = objArr.length;
            int i = this.c;
            int hi = i;
            if (length >= i) {
                int i2 = this.b;
                int i3 = i2;
                if (i2 >= 0) {
                    this.b = hi;
                    if (i3 < hi) {
                        do {
                            consumer.accept(a2[i3]);
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

    public boolean a(Consumer consumer) {
        if (consumer != null) {
            int i = this.b;
            if (i < 0 || i >= this.c) {
                return false;
            }
            T[] tArr = this.a;
            this.b = i + 1;
            consumer.accept(tArr[i]);
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
