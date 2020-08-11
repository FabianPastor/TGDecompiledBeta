package j$.util.concurrent;

import j$.util.N;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

final class g extends q implements Spliterator {
    final ConcurrentHashMap i;
    long j;

    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i2) {
        return N.c(this, i2);
    }

    g(m[] tab, int size, int index, int limit, long est, ConcurrentHashMap concurrentHashMap) {
        super(tab, size, index, limit);
        this.i = concurrentHashMap;
        this.j = est;
    }

    public Spliterator trySplit() {
        int i2 = this.f;
        int i3 = i2;
        int i4 = this.g;
        int f = i4;
        int i5 = (i2 + i4) >>> 1;
        int h = i5;
        if (i5 <= i3) {
            return null;
        }
        m[] mVarArr = this.a;
        int i6 = this.h;
        this.g = h;
        long j2 = this.j >>> 1;
        this.j = j2;
        return new g(mVarArr, i6, h, f, j2, this.i);
    }

    public void forEachRemaining(Consumer consumer) {
        if (consumer != null) {
            while (true) {
                ConcurrentHashMap.Node<K, V> b = b();
                ConcurrentHashMap.Node<K, V> p = b;
                if (b != null) {
                    consumer.accept(new l(p.b, p.c, this.i));
                } else {
                    return;
                }
            }
        } else {
            throw null;
        }
    }

    public boolean a(Consumer consumer) {
        if (consumer != null) {
            ConcurrentHashMap.Node<K, V> b = b();
            ConcurrentHashMap.Node<K, V> p = b;
            if (b == null) {
                return false;
            }
            consumer.accept(new l(p.b, p.c, this.i));
            return true;
        }
        throw null;
    }

    public long estimateSize() {
        return this.j;
    }

    public int characteristics() {
        return 4353;
    }
}
