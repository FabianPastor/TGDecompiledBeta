package j$.util.concurrent;

import j$.r;
import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

final class i extends CLASSNAMEb implements Iterator, Enumeration, j$.util.Iterator {
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        forEachRemaining(r.a(consumer));
    }

    i(m[] tab, int index, int size, int limit, ConcurrentHashMap concurrentHashMap) {
        super(tab, index, size, limit, concurrentHashMap);
    }

    public final Object next() {
        ConcurrentHashMap.Node<K, V> node = this.b;
        ConcurrentHashMap.Node<K, V> p = node;
        if (node != null) {
            K k = p.b;
            this.j = p;
            b();
            return k;
        }
        throw new NoSuchElementException();
    }

    public final Object nextElement() {
        return next();
    }
}