package j$.util.concurrent;

import j$.r;
import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/* renamed from: j$.util.concurrent.e  reason: case insensitive filesystem */
final class CLASSNAMEe extends CLASSNAMEb implements Iterator, j$.util.Iterator {
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        forEachRemaining(r.a(consumer));
    }

    CLASSNAMEe(m[] tab, int index, int size, int limit, ConcurrentHashMap concurrentHashMap) {
        super(tab, index, size, limit, concurrentHashMap);
    }

    /* renamed from: k */
    public final Map.Entry next() {
        ConcurrentHashMap.Node<K, V> node = this.b;
        ConcurrentHashMap.Node<K, V> p = node;
        if (node != null) {
            K k = p.b;
            V v = p.c;
            this.j = p;
            b();
            return new l(k, v, this.i);
        }
        throw new NoSuchElementException();
    }
}
