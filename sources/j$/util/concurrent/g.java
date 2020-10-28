package j$.util.concurrent;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

final class g extends q implements Spliterator {
    final ConcurrentHashMap i;
    long j;

    g(m[] mVarArr, int i2, int i3, int i4, long j2, ConcurrentHashMap concurrentHashMap) {
        super(mVarArr, i2, i3, i4);
        this.i = concurrentHashMap;
        this.j = j2;
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        m a = a();
        if (a == null) {
            return false;
        }
        consumer.accept(new l(a.b, a.c, this.i));
        return true;
    }

    public int characteristics() {
        return 4353;
    }

    public long estimateSize() {
        return this.j;
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        while (true) {
            m a = a();
            if (a != null) {
                consumer.accept(new l(a.b, a.c, this.i));
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
        long j2 = this.j >>> 1;
        this.j = j2;
        return new g(mVarArr, i5, i4, i3, j2, this.i);
    }
}
