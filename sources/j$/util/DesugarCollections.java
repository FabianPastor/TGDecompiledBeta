package j$.util;

import j$.CLASSNAMEq;
import j$.CLASSNAMEs;
import j$.M;
import j$.util.Collection;
import j$.util.List;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.UnaryOperator;
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

    private static class a<K, V> implements Map<K, V>, Serializable, Map {
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
            Object g;
            synchronized (this.b) {
                g = j$.time.a.g(this.a, obj, biFunction);
            }
            return g;
        }

        public Object compute(Object obj, java.util.function.BiFunction biFunction) {
            Object g;
            BiFunction b2 = CLASSNAMEs.b(biFunction);
            synchronized (this.b) {
                g = j$.time.a.g(this.a, obj, b2);
            }
            return g;
        }

        public Object computeIfAbsent(Object obj, Function function) {
            Object h;
            synchronized (this.b) {
                h = j$.time.a.h(this.a, obj, function);
            }
            return h;
        }

        public Object computeIfAbsent(Object obj, java.util.function.Function function) {
            Object h;
            Function c2 = M.c(function);
            synchronized (this.b) {
                h = j$.time.a.h(this.a, obj, c2);
            }
            return h;
        }

        public Object computeIfPresent(Object obj, BiFunction biFunction) {
            Object i;
            synchronized (this.b) {
                i = j$.time.a.i(this.a, obj, biFunction);
            }
            return i;
        }

        public Object computeIfPresent(Object obj, java.util.function.BiFunction biFunction) {
            Object i;
            BiFunction b2 = CLASSNAMEs.b(biFunction);
            synchronized (this.b) {
                i = j$.time.a.i(this.a, obj, b2);
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
                j$.time.a.s(this.a, biConsumer);
            }
        }

        public void forEach(java.util.function.BiConsumer biConsumer) {
            BiConsumer b2 = CLASSNAMEq.b(biConsumer);
            synchronized (this.b) {
                j$.time.a.s(this.a, b2);
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
            Object t;
            synchronized (this.b) {
                t = j$.time.a.t(this.a, obj, obj2);
            }
            return t;
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
                v = j$.time.a.v(this.a, obj, obj2, biFunction);
            }
            return v;
        }

        public Object merge(Object obj, Object obj2, java.util.function.BiFunction biFunction) {
            Object v;
            BiFunction b2 = CLASSNAMEs.b(biFunction);
            synchronized (this.b) {
                v = j$.time.a.v(this.a, obj, obj2, b2);
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
                w = j$.time.a.w(this.a, obj, obj2);
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
                x = j$.time.a.x(this.a, obj, obj2);
            }
            return x;
        }

        public Object replace(Object obj, Object obj2) {
            Object y;
            synchronized (this.b) {
                y = j$.time.a.y(this.a, obj, obj2);
            }
            return y;
        }

        public boolean replace(Object obj, Object obj2, Object obj3) {
            boolean z;
            synchronized (this.b) {
                z = j$.time.a.z(this.a, obj, obj2, obj3);
            }
            return z;
        }

        public void replaceAll(BiFunction biFunction) {
            synchronized (this.b) {
                j$.time.a.A(this.a, biFunction);
            }
        }

        public void replaceAll(java.util.function.BiFunction biFunction) {
            BiFunction b2 = CLASSNAMEs.b(biFunction);
            synchronized (this.b) {
                j$.time.a.A(this.a, b2);
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

    private DesugarCollections() {
    }

    public static void c(Iterable iterable, Consumer consumer) {
        Field field = c;
        if (field == null) {
            try {
                j$.time.a.r((Collection) d.get(iterable), consumer);
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection forEach fall-back.", e2);
            }
        } else {
            try {
                synchronized (field.get(iterable)) {
                    j$.time.a.r((Collection) d.get(iterable), consumer);
                }
            } catch (IllegalAccessException e3) {
                throw new Error("Runtime illegal access in synchronized collection forEach.", e3);
            }
        }
    }

    static boolean d(Collection collection, Predicate predicate) {
        boolean removeIf;
        Field field = c;
        if (field == null) {
            try {
                Collection collection2 = (Collection) d.get(collection);
                return collection2 instanceof Collection ? ((Collection) collection2).removeIf(predicate) : Collection.CC.$default$removeIf(collection2, predicate);
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection removeIf fall-back.", e2);
            }
        } else {
            try {
                synchronized (field.get(collection)) {
                    java.util.Collection collection3 = (java.util.Collection) d.get(collection);
                    removeIf = collection3 instanceof Collection ? ((Collection) collection3).removeIf(predicate) : Collection.CC.$default$removeIf(collection3, predicate);
                }
                return removeIf;
            } catch (IllegalAccessException e3) {
                throw new Error("Runtime illegal access in synchronized collection removeIf.", e3);
            }
        }
    }

    static void e(List list, UnaryOperator unaryOperator) {
        Field field = c;
        if (field == null) {
            try {
                List list2 = (List) d.get(list);
                if (list2 instanceof List) {
                    ((List) list2).replaceAll(unaryOperator);
                } else {
                    List.CC.$default$replaceAll(list2, unaryOperator);
                }
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized list replaceAll fall-back.", e2);
            }
        } else {
            try {
                synchronized (field.get(list)) {
                    java.util.List list3 = (java.util.List) d.get(list);
                    if (list3 instanceof List) {
                        ((List) list3).replaceAll(unaryOperator);
                    } else {
                        List.CC.$default$replaceAll(list3, unaryOperator);
                    }
                }
            } catch (IllegalAccessException e3) {
                throw new Error("Runtime illegal access in synchronized list replaceAll.", e3);
            }
        }
    }

    static void f(java.util.List list, Comparator comparator) {
        Field field = c;
        if (field == null) {
            try {
                j$.time.a.B((java.util.List) d.get(list), comparator);
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection sort fall-back.", e2);
            }
        } else {
            try {
                synchronized (field.get(list)) {
                    j$.time.a.B((java.util.List) d.get(list), comparator);
                }
            } catch (IllegalAccessException e3) {
                throw new Error("Runtime illegal access in synchronized list sort.", e3);
            }
        }
    }

    public static Map synchronizedMap(Map map) {
        return new a(map);
    }
}
