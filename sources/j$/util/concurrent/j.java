package j$.util.concurrent;

import j$.r;
import j$.util.Spliterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class j extends CLASSNAMEc implements Set, Serializable, j$.util.Set {
    private final Object b;

    public /* synthetic */ void forEach(Consumer consumer) {
        forEach(r.a(consumer));
    }

    j(ConcurrentHashMap concurrentHashMap, Object value) {
        super(concurrentHashMap);
        this.b = value;
    }

    public boolean contains(Object o) {
        return this.a.containsKey(o);
    }

    public boolean remove(Object o) {
        return this.a.remove(o) != null;
    }

    public Iterator iterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new i(t, f, 0, f, concurrentHashMap);
    }

    public boolean add(Object e) {
        V v = this.b;
        V v2 = v;
        if (v != null) {
            return this.a.i(e, v2, true) == null;
        }
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        boolean added = false;
        V v = this.b;
        V v2 = v;
        if (v != null) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                if (this.a.i(it.next(), v2, true) == null) {
                    added = true;
                }
            }
            return added;
        }
        throw new UnsupportedOperationException();
    }

    public int hashCode() {
        int h = 0;
        Iterator it = iterator();
        while (((CLASSNAMEb) it).hasNext()) {
            h += ((i) it).next().hashCode();
        }
        return h;
    }

    public boolean equals(Object o) {
        if (o instanceof Set) {
            Set set = (Set) o;
            Set set2 = set;
            if (set == this || (containsAll(set2) && set2.containsAll(this))) {
                return true;
            }
        }
        return false;
    }

    public Spliterator spliterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        long n = concurrentHashMap.n();
        ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new k(t, f, 0, f, n < 0 ? 0 : n);
    }

    public void forEach(j$.util.function.Consumer consumer) {
        if (consumer != null) {
            ConcurrentHashMap.Node<K, V>[] nodeArr = this.a.a;
            ConcurrentHashMap.Node<K, V>[] t = nodeArr;
            if (nodeArr != null) {
                ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
                while (true) {
                    ConcurrentHashMap.Node<K, V> b2 = it.b();
                    ConcurrentHashMap.Node<K, V> p = b2;
                    if (b2 != null) {
                        consumer.accept(p.b);
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
