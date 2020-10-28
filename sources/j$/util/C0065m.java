package j$.util;

import j$.CLASSNAMEt;
import j$.CLASSNAMEv;
import j$.P;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/* renamed from: j$.util.m  reason: case insensitive filesystem */
class CLASSNAMEm implements Map, Serializable, Map {
    private final Map a;
    final Object b = this;
    private transient Set c;
    private transient Set d;
    private transient Collection e;

    CLASSNAMEm(Map map) {
        map.getClass();
        this.a = map;
    }

    private Set a(Set set, Object obj) {
        if (DesugarCollections.f == null) {
            return Collections.synchronizedSet(set);
        }
        try {
            return (Set) DesugarCollections.f.newInstance(new Object[]{set, obj});
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e2) {
            throw new Error("Unable to instantiate a synchronized list.", e2);
        }
    }

    public void clear() {
        synchronized (this.b) {
            this.a.clear();
        }
    }

    public Object compute(Object obj, BiFunction biFunction) {
        Object g;
        synchronized (this.b) {
            g = CLASSNAMEk.g(this.a, obj, biFunction);
        }
        return g;
    }

    public Object compute(Object obj, java.util.function.BiFunction biFunction) {
        Object g;
        BiFunction b2 = CLASSNAMEv.b(biFunction);
        synchronized (this.b) {
            g = CLASSNAMEk.g(this.a, obj, b2);
        }
        return g;
    }

    public Object computeIfAbsent(Object obj, Function function) {
        Object h;
        synchronized (this.b) {
            h = CLASSNAMEk.h(this.a, obj, function);
        }
        return h;
    }

    public Object computeIfAbsent(Object obj, java.util.function.Function function) {
        Object h;
        Function c2 = P.c(function);
        synchronized (this.b) {
            h = CLASSNAMEk.h(this.a, obj, c2);
        }
        return h;
    }

    public Object computeIfPresent(Object obj, BiFunction biFunction) {
        Object i;
        synchronized (this.b) {
            i = CLASSNAMEk.i(this.a, obj, biFunction);
        }
        return i;
    }

    public Object computeIfPresent(Object obj, java.util.function.BiFunction biFunction) {
        Object i;
        BiFunction b2 = CLASSNAMEv.b(biFunction);
        synchronized (this.b) {
            i = CLASSNAMEk.i(this.a, obj, b2);
        }
        return i;
    }

    public boolean containsKey(Object obj) {
        boolean containsKey;
        synchronized (this.b) {
            containsKey = this.a.containsKey(obj);
        }
        return containsKey;
    }

    public boolean containsValue(Object obj) {
        boolean containsValue;
        synchronized (this.b) {
            containsValue = this.a.containsValue(obj);
        }
        return containsValue;
    }

    public Set entrySet() {
        Set set;
        synchronized (this.b) {
            if (this.d == null) {
                this.d = a(this.a.entrySet(), this.b);
            }
            set = this.d;
        }
        return set;
    }

    public boolean equals(Object obj) {
        boolean equals;
        if (this == obj) {
            return true;
        }
        synchronized (this.b) {
            equals = this.a.equals(obj);
        }
        return equals;
    }

    public void forEach(BiConsumer biConsumer) {
        synchronized (this.b) {
            CLASSNAMEk.t(this.a, biConsumer);
        }
    }

    public void forEach(java.util.function.BiConsumer biConsumer) {
        BiConsumer b2 = CLASSNAMEt.b(biConsumer);
        synchronized (this.b) {
            CLASSNAMEk.t(this.a, b2);
        }
    }

    public Object get(Object obj) {
        Object obj2;
        synchronized (this.b) {
            obj2 = this.a.get(obj);
        }
        return obj2;
    }

    public Object getOrDefault(Object obj, Object obj2) {
        Object u;
        synchronized (this.b) {
            u = CLASSNAMEk.u(this.a, obj, obj2);
        }
        return u;
    }

    public int hashCode() {
        int hashCode;
        synchronized (this.b) {
            hashCode = this.a.hashCode();
        }
        return hashCode;
    }

    public boolean isEmpty() {
        boolean isEmpty;
        synchronized (this.b) {
            isEmpty = this.a.isEmpty();
        }
        return isEmpty;
    }

    public Set keySet() {
        Set set;
        synchronized (this.b) {
            if (this.c == null) {
                this.c = a(this.a.keySet(), this.b);
            }
            set = this.c;
        }
        return set;
    }

    public Object merge(Object obj, Object obj2, BiFunction biFunction) {
        Object v;
        synchronized (this.b) {
            v = CLASSNAMEk.v(this.a, obj, obj2, biFunction);
        }
        return v;
    }

    public Object merge(Object obj, Object obj2, java.util.function.BiFunction biFunction) {
        Object v;
        BiFunction b2 = CLASSNAMEv.b(biFunction);
        synchronized (this.b) {
            v = CLASSNAMEk.v(this.a, obj, obj2, b2);
        }
        return v;
    }

    public Object put(Object obj, Object obj2) {
        Object put;
        synchronized (this.b) {
            put = this.a.put(obj, obj2);
        }
        return put;
    }

    public void putAll(Map map) {
        synchronized (this.b) {
            this.a.putAll(map);
        }
    }

    public Object putIfAbsent(Object obj, Object obj2) {
        Object w;
        synchronized (this.b) {
            w = CLASSNAMEk.w(this.a, obj, obj2);
        }
        return w;
    }

    public Object remove(Object obj) {
        Object remove;
        synchronized (this.b) {
            remove = this.a.remove(obj);
        }
        return remove;
    }

    public boolean remove(Object obj, Object obj2) {
        boolean x;
        synchronized (this.b) {
            x = CLASSNAMEk.x(this.a, obj, obj2);
        }
        return x;
    }

    public Object replace(Object obj, Object obj2) {
        Object y;
        synchronized (this.b) {
            y = CLASSNAMEk.y(this.a, obj, obj2);
        }
        return y;
    }

    public boolean replace(Object obj, Object obj2, Object obj3) {
        boolean z;
        synchronized (this.b) {
            z = CLASSNAMEk.z(this.a, obj, obj2, obj3);
        }
        return z;
    }

    public void replaceAll(BiFunction biFunction) {
        synchronized (this.b) {
            CLASSNAMEk.A(this.a, biFunction);
        }
    }

    public void replaceAll(java.util.function.BiFunction biFunction) {
        BiFunction b2 = CLASSNAMEv.b(biFunction);
        synchronized (this.b) {
            CLASSNAMEk.A(this.a, b2);
        }
    }

    public int size() {
        int size;
        synchronized (this.b) {
            size = this.a.size();
        }
        return size;
    }

    public String toString() {
        String obj;
        synchronized (this.b) {
            obj = this.a.toString();
        }
        return obj;
    }

    public Collection values() {
        Collection collection;
        Collection collection2;
        synchronized (this.b) {
            try {
                if (this.e == null) {
                    Collection values = this.a.values();
                    Object obj = this.b;
                    if (DesugarCollections.e == null) {
                        collection2 = Collections.synchronizedCollection(values);
                    } else {
                        collection2 = (Collection) DesugarCollections.e.newInstance(new Object[]{values, obj});
                    }
                    this.e = collection2;
                }
                collection = this.e;
            } catch (InstantiationException e2) {
                e = e2;
                throw new Error("Unable to instantiate a synchronized list.", e);
            } catch (IllegalAccessException e3) {
                e = e3;
                throw new Error("Unable to instantiate a synchronized list.", e);
            } catch (InvocationTargetException e4) {
                e = e4;
                throw new Error("Unable to instantiate a synchronized list.", e);
            } catch (Throwable th) {
                throw th;
            }
        }
        return collection;
    }
}
