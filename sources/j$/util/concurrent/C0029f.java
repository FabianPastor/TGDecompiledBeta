package j$.util.concurrent;

import j$.r;
import j$.util.Spliterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/* renamed from: j$.util.concurrent.f  reason: case insensitive filesystem */
final class CLASSNAMEf extends CLASSNAMEc implements Set, Serializable, j$.util.Set {
    public /* synthetic */ void forEach(Consumer consumer) {
        forEach(r.a(consumer));
    }

    CLASSNAMEf(ConcurrentHashMap concurrentHashMap) {
        super(concurrentHashMap);
    }

    public boolean contains(Object o) {
        if (o instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry) o;
            Map.Entry<?, ?> e = entry;
            Object key = entry.getKey();
            Object k = key;
            if (key != null) {
                Object obj = this.a.get(k);
                Object r = obj;
                if (obj != null) {
                    Object value = e.getValue();
                    Object v = value;
                    return value != null && (v == r || v.equals(r));
                }
            }
        }
    }

    public boolean remove(Object o) {
        if (o instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry) o;
            Map.Entry<?, ?> e = entry;
            Object key = entry.getKey();
            Object k = key;
            if (key != null) {
                Object v = e.getValue();
                return v != null && this.a.remove(k, v);
            }
        }
    }

    public Iterator iterator() {
        ConcurrentHashMap concurrentHashMap = this.a;
        ConcurrentHashMap.Node<K, V>[] nodeArr = concurrentHashMap.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        int f = nodeArr == null ? 0 : t.length;
        return new CLASSNAMEe(t, f, 0, f, concurrentHashMap);
    }

    /* renamed from: a */
    public boolean add(Map.Entry e) {
        return this.a.i(e.getKey(), e.getValue(), false) == null;
    }

    public boolean addAll(Collection c) {
        boolean added = false;
        Iterator it = c.iterator();
        while (it.hasNext()) {
            if (add((Map.Entry) it.next())) {
                added = true;
            }
        }
        return added;
    }

    public final int hashCode() {
        int h = 0;
        ConcurrentHashMap.Node<K, V>[] nodeArr = this.a.a;
        ConcurrentHashMap.Node<K, V>[] t = nodeArr;
        if (nodeArr != null) {
            ConcurrentHashMap.Traverser<K, V> it = new q(t, t.length, 0, t.length);
            while (true) {
                m b = it.b();
                m mVar = b;
                if (b == null) {
                    break;
                }
                h += mVar.hashCode();
            }
        }
        return h;
    }

    public final boolean equals(Object o) {
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
        return new g(t, f, 0, f, n < 0 ? 0 : n, concurrentHashMap);
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
                        consumer.accept(new l(p.b, p.c, this.a));
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
