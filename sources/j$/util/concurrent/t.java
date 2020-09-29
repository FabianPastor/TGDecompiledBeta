package j$.util.concurrent;

import j$.C;
import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

final class t extends CLASSNAMEb implements Iterator, Enumeration, j$.util.Iterator {
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        forEachRemaining(C.a(consumer));
    }

    t(m[] tab, int index, int size, int limit, ConcurrentHashMap concurrentHashMap) {
        super(tab, index, size, limit, concurrentHashMap);
    }

    public final Object next() {
        ConcurrentHashMap.Node<K, V> node = this.b;
        ConcurrentHashMap.Node<K, V> p = node;
        if (node != null) {
            V v = p.c;
            this.j = p;
            b();
            return v;
        }
        throw new NoSuchElementException();
    }

    public final Object nextElement() {
        return next();
    }
}
