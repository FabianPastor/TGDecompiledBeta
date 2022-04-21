package j$.util;

import j$.util.Map;
import j$.wrappers.C$r8$wrapper$java$util$function$BiConsumer$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$BiFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$Function$VWRP;
import java.io.ObjectOutputStream;
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
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DesugarCollections {
    private static final Field COLLECTION_FIELD;
    private static final Field MUTEX_FIELD;
    public static final Class<? extends Collection> SYNCHRONIZED_COLLECTION;
    /* access modifiers changed from: private */
    public static final Constructor<? extends Collection> SYNCHRONIZED_COLLECTION_CONSTRUCTOR;
    static final Class<? extends List> SYNCHRONIZED_LIST = Collections.synchronizedList(new LinkedList()).getClass();
    /* access modifiers changed from: private */
    public static final Constructor<? extends Set> SYNCHRONIZED_SET_CONSTRUCTOR;

    private DesugarCollections() {
    }

    static {
        Class<?> cls = Collections.synchronizedCollection(new ArrayList()).getClass();
        SYNCHRONIZED_COLLECTION = cls;
        Field field = getField(cls, "mutex");
        MUTEX_FIELD = field;
        if (field != null) {
            field.setAccessible(true);
        }
        Field field2 = getField(cls, "c");
        COLLECTION_FIELD = field2;
        if (field2 != null) {
            field2.setAccessible(true);
        }
        Constructor<? extends Set> constructor = getConstructor(Collections.synchronizedSet(new HashSet()).getClass(), Set.class, Object.class);
        SYNCHRONIZED_SET_CONSTRUCTOR = constructor;
        if (constructor != null) {
            constructor.setAccessible(true);
        }
        Constructor<? extends Collection> constructor2 = getConstructor(cls, Collection.class, Object.class);
        SYNCHRONIZED_COLLECTION_CONSTRUCTOR = constructor2;
        if (constructor2 != null) {
            constructor2.setAccessible(true);
        }
    }

    private static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static <E> Constructor<? extends E> getConstructor(Class<? extends E> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0030, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static <E> boolean removeIf(java.util.Collection<E> r3, j$.util.function.Predicate<? super E> r4) {
        /*
            java.lang.reflect.Field r0 = MUTEX_FIELD
            if (r0 != 0) goto L_0x001a
            java.lang.reflect.Field r0 = COLLECTION_FIELD     // Catch:{ IllegalAccessException -> 0x0011 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0011 }
            java.util.Collection r0 = (java.util.Collection) r0     // Catch:{ IllegalAccessException -> 0x0011 }
            boolean r0 = j$.util.Collection.EL.removeIf(r0, r4)     // Catch:{ IllegalAccessException -> 0x0011 }
            return r0
        L_0x0011:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection removeIf fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x001a:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0032 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0032 }
            java.lang.reflect.Field r1 = COLLECTION_FIELD     // Catch:{ all -> 0x002d }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002d }
            java.util.Collection r1 = (java.util.Collection) r1     // Catch:{ all -> 0x002d }
            boolean r1 = j$.util.Collection.EL.removeIf(r1, r4)     // Catch:{ all -> 0x002d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return r1
        L_0x002d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r1     // Catch:{ IllegalAccessException -> 0x0030 }
        L_0x0030:
            r0 = move-exception
            goto L_0x0033
        L_0x0032:
            r0 = move-exception
        L_0x0033:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection removeIf."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.removeIf(java.util.Collection, j$.util.function.Predicate):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <E> void forEach(java.lang.Iterable<E> r3, j$.util.function.Consumer<? super E> r4) {
        /*
            java.lang.reflect.Field r0 = MUTEX_FIELD
            if (r0 != 0) goto L_0x0019
            java.lang.reflect.Field r0 = COLLECTION_FIELD     // Catch:{ IllegalAccessException -> 0x0010 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0010 }
            java.util.Collection r0 = (java.util.Collection) r0     // Catch:{ IllegalAccessException -> 0x0010 }
            j$.util.Collection.EL.forEach(r0, r4)     // Catch:{ IllegalAccessException -> 0x0010 }
            return
        L_0x0010:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection forEach fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x0019:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0031 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0031 }
            java.lang.reflect.Field r1 = COLLECTION_FIELD     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002c }
            java.util.Collection r1 = (java.util.Collection) r1     // Catch:{ all -> 0x002c }
            j$.util.Collection.EL.forEach(r1, r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1     // Catch:{ IllegalAccessException -> 0x002f }
        L_0x002f:
            r0 = move-exception
            goto L_0x0032
        L_0x0031:
            r0 = move-exception
        L_0x0032:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection forEach."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.forEach(java.lang.Iterable, j$.util.function.Consumer):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static <E> void replaceAll(java.util.List<E> r3, j$.util.function.UnaryOperator<E> r4) {
        /*
            java.lang.reflect.Field r0 = MUTEX_FIELD
            if (r0 != 0) goto L_0x0019
            java.lang.reflect.Field r0 = COLLECTION_FIELD     // Catch:{ IllegalAccessException -> 0x0010 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0010 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ IllegalAccessException -> 0x0010 }
            j$.util.List.EL.replaceAll(r0, r4)     // Catch:{ IllegalAccessException -> 0x0010 }
            return
        L_0x0010:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized list replaceAll fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x0019:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0031 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0031 }
            java.lang.reflect.Field r1 = COLLECTION_FIELD     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002c }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x002c }
            j$.util.List.EL.replaceAll(r1, r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1     // Catch:{ IllegalAccessException -> 0x002f }
        L_0x002f:
            r0 = move-exception
            goto L_0x0032
        L_0x0031:
            r0 = move-exception
        L_0x0032:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized list replaceAll."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.replaceAll(java.util.List, j$.util.function.UnaryOperator):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static <E> void sort(java.util.List<E> r3, java.util.Comparator<? super E> r4) {
        /*
            java.lang.reflect.Field r0 = MUTEX_FIELD
            if (r0 != 0) goto L_0x0019
            java.lang.reflect.Field r0 = COLLECTION_FIELD     // Catch:{ IllegalAccessException -> 0x0010 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0010 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ IllegalAccessException -> 0x0010 }
            j$.util.List.EL.sort(r0, r4)     // Catch:{ IllegalAccessException -> 0x0010 }
            return
        L_0x0010:
            r0 = move-exception
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized collection sort fall-back."
            r1.<init>(r2, r0)
            throw r1
        L_0x0019:
            java.lang.Object r0 = r0.get(r3)     // Catch:{ IllegalAccessException -> 0x0031 }
            monitor-enter(r0)     // Catch:{ IllegalAccessException -> 0x0031 }
            java.lang.reflect.Field r1 = COLLECTION_FIELD     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x002c }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x002c }
            j$.util.List.EL.sort(r1, r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1     // Catch:{ IllegalAccessException -> 0x002f }
        L_0x002f:
            r0 = move-exception
            goto L_0x0032
        L_0x0031:
            r0 = move-exception
        L_0x0032:
            java.lang.Error r1 = new java.lang.Error
            java.lang.String r2 = "Runtime illegal access in synchronized list sort."
            r1.<init>(r2, r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.DesugarCollections.sort(java.util.List, java.util.Comparator):void");
    }

    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> m) {
        return new SynchronizedMap(m);
    }

    private static class SynchronizedMap<K, V> implements Map<K, V>, Serializable, Map<K, V> {
        private static final long serialVersionUID = 1978198479659022715L;
        private transient Set<Map.Entry<K, V>> entrySet;
        private transient Set<K> keySet;
        private final Map<K, V> m;
        final Object mutex;
        private transient Collection<V> values;

        public /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
            return compute(obj, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
            return computeIfAbsent(obj, C$r8$wrapper$java$util$function$Function$VWRP.convert(function));
        }

        public /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
            return computeIfPresent(obj, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        public /* synthetic */ void forEach(BiConsumer biConsumer) {
            forEach(C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer));
        }

        public /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
            return merge(obj, obj2, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        public /* synthetic */ void replaceAll(BiFunction biFunction) {
            replaceAll(C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        SynchronizedMap(Map<K, V> m2) {
            m2.getClass();
            this.m = m2;
            this.mutex = this;
        }

        SynchronizedMap(Map<K, V> m2, Object mutex2) {
            this.m = m2;
            this.mutex = mutex2;
        }

        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.m.size();
            }
            return size;
        }

        public boolean isEmpty() {
            boolean isEmpty;
            synchronized (this.mutex) {
                isEmpty = this.m.isEmpty();
            }
            return isEmpty;
        }

        public boolean containsKey(Object key) {
            boolean containsKey;
            synchronized (this.mutex) {
                containsKey = this.m.containsKey(key);
            }
            return containsKey;
        }

        public boolean containsValue(Object value) {
            boolean containsValue;
            synchronized (this.mutex) {
                containsValue = this.m.containsValue(value);
            }
            return containsValue;
        }

        public V get(Object key) {
            V v;
            synchronized (this.mutex) {
                v = this.m.get(key);
            }
            return v;
        }

        public V put(K key, V value) {
            V put;
            synchronized (this.mutex) {
                put = this.m.put(key, value);
            }
            return put;
        }

        public V remove(Object key) {
            V remove;
            synchronized (this.mutex) {
                remove = this.m.remove(key);
            }
            return remove;
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (this.mutex) {
                this.m.putAll(map);
            }
        }

        public void clear() {
            synchronized (this.mutex) {
                this.m.clear();
            }
        }

        private <T> Set<T> instantiateSet(Set<T> set, Object mutex2) {
            if (DesugarCollections.SYNCHRONIZED_SET_CONSTRUCTOR == null) {
                return Collections.synchronizedSet(set);
            }
            try {
                return (Set) DesugarCollections.SYNCHRONIZED_SET_CONSTRUCTOR.newInstance(new Object[]{set, mutex2});
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new Error("Unable to instantiate a synchronized list.", e);
            }
        }

        private <T> Collection<T> instantiateCollection(Collection<T> collection, Object mutex2) {
            if (DesugarCollections.SYNCHRONIZED_COLLECTION_CONSTRUCTOR == null) {
                return Collections.synchronizedCollection(collection);
            }
            try {
                return (Collection) DesugarCollections.SYNCHRONIZED_COLLECTION_CONSTRUCTOR.newInstance(new Object[]{collection, mutex2});
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new Error("Unable to instantiate a synchronized list.", e);
            }
        }

        public Set<K> keySet() {
            Set<K> set;
            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = instantiateSet(this.m.keySet(), this.mutex);
                }
                set = this.keySet;
            }
            return set;
        }

        public Set<Map.Entry<K, V>> entrySet() {
            Set<Map.Entry<K, V>> set;
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = instantiateSet(this.m.entrySet(), this.mutex);
                }
                set = this.entrySet;
            }
            return set;
        }

        public Collection<V> values() {
            Collection<V> collection;
            synchronized (this.mutex) {
                if (this.values == null) {
                    this.values = instantiateCollection(this.m.values(), this.mutex);
                }
                collection = this.values;
            }
            return collection;
        }

        public boolean equals(Object o) {
            boolean equals;
            if (this == o) {
                return true;
            }
            synchronized (this.mutex) {
                equals = this.m.equals(o);
            }
            return equals;
        }

        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.m.hashCode();
            }
            return hashCode;
        }

        public String toString() {
            String obj;
            synchronized (this.mutex) {
                obj = this.m.toString();
            }
            return obj;
        }

        public V getOrDefault(Object k, V defaultValue) {
            V orDefault;
            synchronized (this.mutex) {
                orDefault = Map.EL.getOrDefault(this.m, k, defaultValue);
            }
            return orDefault;
        }

        public void forEach(j$.util.function.BiConsumer<? super K, ? super V> biConsumer) {
            synchronized (this.mutex) {
                Map.EL.forEach(this.m, biConsumer);
            }
        }

        public void replaceAll(j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
            synchronized (this.mutex) {
                Map.EL.replaceAll(this.m, biFunction);
            }
        }

        public V putIfAbsent(K key, V value) {
            V putIfAbsent;
            synchronized (this.mutex) {
                putIfAbsent = Map.EL.putIfAbsent(this.m, key, value);
            }
            return putIfAbsent;
        }

        public boolean remove(Object key, Object value) {
            boolean remove;
            synchronized (this.mutex) {
                remove = Map.EL.remove(this.m, key, value);
            }
            return remove;
        }

        public boolean replace(K key, V oldValue, V newValue) {
            boolean replace;
            synchronized (this.mutex) {
                replace = Map.EL.replace(this.m, key, oldValue, newValue);
            }
            return replace;
        }

        public V replace(K key, V value) {
            V replace;
            synchronized (this.mutex) {
                replace = Map.EL.replace(this.m, key, value);
            }
            return replace;
        }

        public V computeIfAbsent(K key, j$.util.function.Function<? super K, ? extends V> function) {
            V computeIfAbsent;
            synchronized (this.mutex) {
                computeIfAbsent = Map.EL.computeIfAbsent(this.m, key, function);
            }
            return computeIfAbsent;
        }

        public V computeIfPresent(K key, j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
            V computeIfPresent;
            synchronized (this.mutex) {
                computeIfPresent = Map.EL.computeIfPresent(this.m, key, biFunction);
            }
            return computeIfPresent;
        }

        public V compute(K key, j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
            V compute;
            synchronized (this.mutex) {
                compute = Map.EL.compute(this.m, key, biFunction);
            }
            return compute;
        }

        public V merge(K key, V value, j$.util.function.BiFunction<? super V, ? super V, ? extends V> biFunction) {
            V merge;
            synchronized (this.mutex) {
                merge = Map.EL.merge(this.m, key, value, biFunction);
            }
            return merge;
        }

        private void writeObject(ObjectOutputStream s) {
            synchronized (this.mutex) {
                s.defaultWriteObject();
            }
        }
    }

    public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> m) {
        return new SynchronizedSortedMap(m);
    }

    static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V>, Map<K, V> {
        private static final long serialVersionUID = -8798146769416483793L;
        private final SortedMap<K, V> sm;

        SynchronizedSortedMap(SortedMap<K, V> m) {
            super(m);
            this.sm = m;
        }

        SynchronizedSortedMap(SortedMap<K, V> m, Object mutex) {
            super(m, mutex);
            this.sm = m;
        }

        public Comparator<? super K> comparator() {
            Comparator<? super K> comparator;
            synchronized (this.mutex) {
                comparator = this.sm.comparator();
            }
            return comparator;
        }

        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.subMap(fromKey, toKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        public SortedMap<K, V> headMap(K toKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.headMap(toKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        public SortedMap<K, V> tailMap(K fromKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.tailMap(fromKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        public K firstKey() {
            K firstKey;
            synchronized (this.mutex) {
                firstKey = this.sm.firstKey();
            }
            return firstKey;
        }

        public K lastKey() {
            K lastKey;
            synchronized (this.mutex) {
                lastKey = this.sm.lastKey();
            }
            return lastKey;
        }
    }
}
