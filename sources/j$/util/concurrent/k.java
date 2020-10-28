package j$.util.concurrent;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

final class k extends q implements Spliterator {
    long i;

    k(m[] mVarArr, int i2, int i3, int i4, long j) {
        super(mVarArr, i2, i3, i4);
        this.i = j;
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        m a = a();
        if (a == null) {
            return false;
        }
        consumer.accept(a.b);
        return true;
    }

    public int characteristics() {
        return 4353;
    }

    public long estimateSize() {
        return this.i;
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        while (true) {
            m a = a();
            if (a != null) {
                consumer.accept(a.b);
            } else {
                return;
            }
        }
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return CLASSNAMEk.f(this, i2);
    }

    public Spliterator trySplit() {
        int i2 = this.f;
        int i3 = this.g;
        int i4 = (i2 + i3) >>> 1;
        if (i4 <= i2) {
            return null;
        }
        m[] mVarArr = this.a;
        int i5 = this.h;
        this.g = i4;
        long j = this.i >>> 1;
        this.i = j;
        return new k(mVarArr, i5, i4, i3, j);
    }
}
