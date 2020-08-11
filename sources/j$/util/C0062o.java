package j$.util;

import j$.CLASSNAMEn;
import j$.CLASSNAMEp;
import j$.D;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/* renamed from: j$.util.o  reason: case insensitive filesystem */
class CLASSNAMEo implements Map, Serializable, Map {
    private final Map a;
    final Object b = this;
    private transient Set c;
    private transient Set d;
    private transient Collection e;

    public /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
        return compute(obj, CLASSNAMEp.b(biFunction));
    }

    public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return computeIfAbsent(obj, D.c(function));
    }

    public /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
        return computeIfPresent(obj, CLASSNAMEp.b(biFunction));
    }

    public /* synthetic */ void forEach(BiConsumer biConsumer) {
        forEach(CLASSNAMEn.b(biConsumer));
    }

    public /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
        return merge(obj, obj2, CLASSNAMEp.b(biFunction));
    }

    public /* synthetic */ void replaceAll(BiFunction biFunction) {
        replaceAll(CLASSNAMEp.b(biFunction));
    }

    CLASSNAMEo(Map m) {
        m.getClass();
        this.a = m;
    }

    public int size() {
        int size;
        synchronized (this.b) {
            size = this.a.size();
        }
        return size;
    }

    public boolean isEmpty() {
        boolean isEmpty;
        synchronized (this.b) {
            isEmpty = this.a.isEmpty();
        }
        return isEmpty;
    }

    public boolean containsKey(Object key) {
        boolean containsKey;
        synchronized (this.b) {
            containsKey = this.a.containsKey(key);
        }
        return containsKey;
    }

    public boolean containsValue(Object value) {
        boolean containsValue;
        synchronized (this.b) {
            containsValue = this.a.containsValue(value);
        }
        return containsValue;
    }

    public Object get(Object key) {
        Object obj;
        synchronized (this.b) {
            obj = this.a.get(key);
        }
        return obj;
    }

    public Object put(Object key, Object value) {
        Object put;
        synchronized (this.b) {
            put = this.a.put(key, value);
        }
        return put;
    }

    public Object remove(Object key) {
        Object remove;
        synchronized (this.b) {
            remove = this.a.remove(key);
        }
        return remove;
    }

    public void putAll(Map map) {
        synchronized (this.b) {
            this.a.putAll(map);
        }
    }

    public void clear() {
        synchronized (this.b) {
            this.a.clear();
        }
    }

    private Set b(Set set, Object mutex) {
        if (DesugarCollections.f == null) {
            return Collections.synchronizedSet(set);
        }
        try {
            return (Set) DesugarCollections.f.newInstance(new Object[]{set, mutex});
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
            throw new Error("Unable to instantiate a synchronized list.", e2);
        }
    }

    private Collection a(Collection collection, Object mutex) {
        if (DesugarCollections.e == null) {
            return Collections.synchronizedCollection(collection);
        }
        try {
            return (Collection) DesugarCollections.e.newInstance(new Object[]{collection, mutex});
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
            throw new Error("Unable to instantiate a synchronized list.", e2);
        }
    }

    public Set keySet() {
        Set set;
        synchronized (this.b) {
            if (this.c == null) {
                this.c = b(this.a.keySet(), this.b);
            }
            set = this.c;
        }
        return set;
    }

    public Set entrySet() {
        Set set;
        synchronized (this.b) {
            if (this.d == null) {
                this.d = b(this.a.entrySet(), this.b);
            }
            set = this.d;
        }
        return set;
    }

    public Collection values() {
        Collection collection;
        synchronized (this.b) {
            if (this.e == null) {
                this.e = a(this.a.values(), this.b);
            }
            collection = this.e;
        }
        return collection;
    }

    public boolean equals(Object o) {
        boolean equals;
        if (this == o) {
            return true;
        }
        synchronized (this.b) {
            equals = this.a.equals(o);
        }
        return equals;
    }

    public int hashCode() {
        int hashCode;
        synchronized (this.b) {
            hashCode = this.a.hashCode();
        }
        return hashCode;
    }

    public String toString() {
        String obj;
        synchronized (this.b) {
            obj = this.a.toString();
        }
        return obj;
    }

    public Object getOrDefault(Object k, Object defaultValue) {
        Object e2;
        synchronized (this.b) {
            e2 = CLASSNAMEy.e(this.a, k, defaultValue);
        }
        return e2;
    }

    public void forEach(j$.util.function.BiConsumer biConsumer) {
        synchronized (this.b) {
            CLASSNAMEy.d(this.a, biConsumer);
        }
    }

    public void replaceAll(j$.util.function.BiFunction biFunction) {
        synchronized (this.b) {
            CLASSNAMEy.k(this.a, biFunction);
        }
    }

    public Object putIfAbsent(Object key, Object value) {
        Object g;
        synchronized (this.b) {
            g = CLASSNAMEy.g(this.a, key, value);
        }
        return g;
    }

    public boolean remove(Object key, Object value) {
        boolean h;
        synchronized (this.b) {
            h = CLASSNAMEy.h(this.a, key, value);
        }
        return h;
    }

    public boolean replace(Object key, Object oldValue, Object newValue) {
        boolean j;
        synchronized (this.b) {
            j = CLASSNAMEy.j(this.a, key, oldValue, newValue);
        }
        return j;
    }

    public Object replace(Object key, Object value) {
        Object i;
        synchronized (this.b) {
            i = CLASSNAMEy.i(this.a, key, value);
        }
        return i;
    }

    public Object computeIfAbsent(Object key, j$.util.function.Function function) {
        Object b2;
        synchronized (this.b) {
            b2 = CLASSNAMEy.b(this.a, key, function);
        }
        return b2;
    }

    public Object computeIfPresent(Object key, j$.util.function.BiFunction biFunction) {
        Object c2;
        synchronized (this.b) {
            c2 = CLASSNAMEy.c(this.a, key, biFunction);
        }
        return c2;
    }

    public Object compute(Object key, j$.util.function.BiFunction biFunction) {
        Object a2;
        synchronized (this.b) {
            a2 = CLASSNAMEy.a(this.a, key, biFunction);
        }
        return a2;
    }

    public Object merge(Object key, Object value, j$.util.function.BiFunction biFunction) {
        Object f;
        synchronized (this.b) {
            f = CLASSNAMEy.f(this.a, key, value, biFunction);
        }
        return f;
    }
}
