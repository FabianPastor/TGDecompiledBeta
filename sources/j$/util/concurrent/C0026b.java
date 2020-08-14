package j$.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/* renamed from: j$.util.concurrent.b  reason: case insensitive filesystem */
class CLASSNAMEb extends q {
    final ConcurrentHashMap i;
    m j;

    CLASSNAMEb(m[] tab, int size, int index, int limit, ConcurrentHashMap concurrentHashMap) {
        super(tab, size, index, limit);
        this.i = concurrentHashMap;
        b();
    }

    public final boolean hasNext() {
        return this.b != null;
    }

    public final boolean hasMoreElements() {
        return this.b != null;
    }

    public final void remove() {
        ConcurrentHashMap.Node<K, V> node = this.j;
        ConcurrentHashMap.Node<K, V> p = node;
        if (node != null) {
            this.j = null;
            this.i.j(p.b, (Object) null, (Object) null);
            return;
        }
        throw new IllegalStateException();
    }
}
