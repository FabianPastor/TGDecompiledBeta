package j$.util;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import j$.util.function.y;
import j$.wrappers.CLASSNAMEq;
import j$.wrappers.CLASSNAMEs;
import j$.wrappers.M;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DesugarCollections {
    public static final Class a;
    static final Class b = Collections.synchronizedList(new LinkedList()).getClass();
    private static final Field c;
    private static final Field d;
    /* access modifiers changed from: private */
    public static final Constructor e;
    /* access modifiers changed from: private */
    public static final Constructor f;

    private static class a implements Map, Serializable, Map {
        private final Map a;
        final Object b = this;
        private transient Set c;
        private transient Set d;
        private transient Collection e;

        a(Map map) {
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
            Object m;
            synchronized (this.b) {
                m = CLASSNAMEa.m(this.a, obj, biFunction);
            }
            return m;
        }

        public Object compute(Object obj, java.util.function.BiFunction biFunction) {
            Object m;
            BiFunction a2 = CLASSNAMEs.a(biFunction);
            synchronized (this.b) {
                m = CLASSNAMEa.m(this.a, obj, a2);
            }
            return m;
        }

        public Object computeIfAbsent(Object obj, Function function) {
            Object n;
            synchronized (this.b) {
                n = CLASSNAMEa.n(this.a, obj, function);
            }
            return n;
        }

        public Object computeIfAbsent(Object obj, java.util.function.Function function) {
            Object n;
            Function a2 = M.a(function);
            synchronized (this.b) {
                n = CLASSNAMEa.n(this.a, obj, a2);
            }
            return n;
        }

        public Object computeIfPresent(Object obj, BiFunction biFunction) {
            Object o;
            synchronized (this.b) {
                o = CLASSNAMEa.o(this.a, obj, biFunction);
            }
            return o;
        }

        public Object computeIfPresent(Object obj, java.util.function.BiFunction biFunction) {
            Object o;
            BiFunction a2 = CLASSNAMEs.a(biFunction);
            synchronized (this.b) {
                o = CLASSNAMEa.o(this.a, obj, a2);
            }
            return o;
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
                CLASSNAMEa.z(this.a, biConsumer);
            }
        }

        public void forEach(java.util.function.BiConsumer biConsumer) {
            BiConsumer a2 = CLASSNAMEq.a(biConsumer);
            synchronized (this.b) {
                CLASSNAMEa.z(this.a, a2);
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
            Object A;
            synchronized (this.b) {
                A = CLASSNAMEa.A(this.a, obj, obj2);
            }
            return A;
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
            Object B;
            synchronized (this.b) {
                B = CLASSNAMEa.B(this.a, obj, obj2, biFunction);
            }
            return B;
        }

        public Object merge(Object obj, Object obj2, java.util.function.BiFunction biFunction) {
            Object B;
            BiFunction a2 = CLASSNAMEs.a(biFunction);
            synchronized (this.b) {
                B = CLASSNAMEa.B(this.a, obj, obj2, a2);
            }
            return B;
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
            Object C;
            synchronized (this.b) {
                C = CLASSNAMEa.C(this.a, obj, obj2);
            }
            return C;
        }

        public Object remove(Object obj) {
            Object remove;
            synchronized (this.b) {
                remove = this.a.remove(obj);
            }
            return remove;
        }

        public boolean remove(Object obj, Object obj2) {
            boolean D;
            synchronized (this.b) {
                D = CLASSNAMEa.D(this.a, obj, obj2);
            }
            return D;
        }

        public Object replace(Object obj, Object obj2) {
            Object E;
            synchronized (this.b) {
                E = CLASSNAMEa.E(this.a, obj, obj2);
            }
            return E;
        }

        public boolean replace(Object obj, Object obj2, Object obj3) {
            boolean F;
            synchronized (this.b) {
                F = CLASSNAMEa.F(this.a, obj, obj2, obj3);
            }
            return F;
        }

        public void replaceAll(BiFunction biFunction) {
            synchronized (this.b) {
                CLASSNAMEa.G(this.a, biFunction);
            }
        }

        public void replaceAll(java.util.function.BiFunction biFunction) {
            BiFunction a2 = CLASSNAMEs.a(biFunction);
            synchronized (this.b) {
                CLASSNAMEa.G(this.a, a2);
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

    static {
        Field field;
        Field field2;
        Constructor<?> constructor;
        Class<Object> cls = Object.class;
        Class<?> cls2 = Collections.synchronizedCollection(new ArrayList()).getClass();
        a = cls2;
        Constructor<?> constructor2 = null;
        try {
            field = cls2.getDeclaredField("mutex");
        } catch (NoSuchFieldException unused) {
            field = null;
        }
        c = field;
        if (field != null) {
            field.setAccessible(true);
        }
        try {
            field2 = cls2.getDeclaredField("c");
        } catch (NoSuchFieldException unused2) {
            field2 = null;
        }
        d = field2;
        if (field2 != null) {
            field2.setAccessible(true);
        }
        try {
            constructor = Collections.synchronizedSet(new HashSet()).getClass().getDeclaredConstructor(new Class[]{Set.class, cls});
        } catch (NoSuchMethodException unused3) {
            constructor = null;
        }
        f = constructor;
        if (constructor != null) {
            constructor.setAccessible(true);
        }
        try {
            constructor2 = cls2.getDeclaredConstructor(new Class[]{Collection.class, cls});
        } catch (NoSuchMethodException unused4) {
        }
        e = constructor2;
        if (constructor2 != null) {
            constructor2.setAccessible(true);
        }
    }

    static boolean c(Collection collection, y yVar) {
        boolean k;
        Field field = c;
        if (field == null) {
            try {
                Collection collection2 = (Collection) d.get(collection);
                return collection2 instanceof CLASSNAMEb ? ((CLASSNAMEb) collection2).k(yVar) : CLASSNAMEa.h(collection2, yVar);
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection removeIf fall-back.", e2);
            }
        } else {
            try {
                synchronized (field.get(collection)) {
                    Collection collection3 = (Collection) d.get(collection);
                    k = collection3 instanceof CLASSNAMEb ? ((CLASSNAMEb) collection3).k(yVar) : CLASSNAMEa.h(collection3, yVar);
                }
                return k;
            } catch (IllegalAccessException e3) {
                throw new Error("Runtime illegal access in synchronized collection removeIf.", e3);
            }
        }
    }

    static void d(List list, Comparator comparator) {
        Field field = c;
        if (field == null) {
            try {
                CLASSNAMEa.H((List) d.get(list), comparator);
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection sort fall-back.", e2);
            }
        } else {
            try {
                synchronized (field.get(list)) {
                    CLASSNAMEa.H((List) d.get(list), comparator);
                }
            } catch (IllegalAccessException e3) {
                throw new Error("Runtime illegal access in synchronized list sort.", e3);
            }
        }
    }

    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
        return new a(map);
    }
}
