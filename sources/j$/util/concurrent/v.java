package j$.util.concurrent;

import j$.C;
import j$.util.Spliterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

final class v extends CLASSNAMEc implements Collection, Serializable, j$.util.Collection {
    public /* synthetic */ void forEach(Consumer consumer) {
        forEach(C.a(consumer));
    }

    v(ConcurrentHashMap concurrentHashMap) {
        super(concurrentHashMap);
    }

    public final boolean contains(Object o) {
        return this.a.containsValue(o);
    }

    public final boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        Iterator<V> it = iterator();
        while (((CLASSNAMEb) it).hasNext()) {
            if (o.equals(((t) it).next())) {
                ((CLASSNAMEb) it).remove();
                return true;
            }
        }
        return false;
    }

    public final Iterator iterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new t(t, f, 0, f, concurrentHashMap);
    }

    public final boolean add(Object obj) {
        throw new UnsupportedOperationException();
    }

    public final boolean addAll(Collection collection) {
        throw new UnsupportedOperationException();
    }

    public Spliterator spliterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        long n = concurrentHashMap.n();
        ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new u(t, f, 0, f, n < 0 ? 0 : n);
    }

    public void forEach(j$.util.function.Consumer consumer) {
        if (consumer != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.a.a;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> b = it.b();
                    ConcurrentHashMap.Node<K, V> p = b;
                    if (b != null) {
                        consumer.accept(p.c);
                    } else {
                        return;
                    }
                }
            }
        } else {
            throw null;
        }
    }
}
