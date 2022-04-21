package j$.util;

import j$.util.concurrent.ConcurrentMap;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public interface Map<K, V> {

    /* renamed from: j$.util.Map$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Object compute(java.util.Map map, Object obj, BiFunction biFunction) {
            return map instanceof Map ? ((Map) map).compute(obj, biFunction) : map instanceof ConcurrentMap ? ConcurrentMap.CC.$default$compute((java.util.concurrent.ConcurrentMap) map, obj, biFunction) : CC.$default$compute(map, obj, biFunction);
        }

        public static /* synthetic */ Object computeIfAbsent(java.util.Map map, Object obj, Function function) {
            return map instanceof Map ? ((Map) map).computeIfAbsent(obj, function) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$computeIfAbsent((java.util.concurrent.ConcurrentMap) map, obj, function) : CC.$default$computeIfAbsent(map, obj, function);
        }

        public static /* synthetic */ Object computeIfPresent(java.util.Map map, Object obj, BiFunction biFunction) {
            return map instanceof Map ? ((Map) map).computeIfPresent(obj, biFunction) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$computeIfPresent((java.util.concurrent.ConcurrentMap) map, obj, biFunction) : CC.$default$computeIfPresent(map, obj, biFunction);
        }

        public static /* synthetic */ void forEach(java.util.Map map, BiConsumer biConsumer) {
            if (map instanceof Map) {
                ((Map) map).forEach(biConsumer);
            } else if (map instanceof java.util.concurrent.ConcurrentMap) {
                ConcurrentMap.CC.$default$forEach((java.util.concurrent.ConcurrentMap) map, biConsumer);
            } else {
                CC.$default$forEach(map, biConsumer);
            }
        }

        public static /* synthetic */ Object getOrDefault(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).getOrDefault(obj, obj2) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$getOrDefault((java.util.concurrent.ConcurrentMap) map, obj, obj2) : CC.$default$getOrDefault(map, obj, obj2);
        }

        public static /* synthetic */ Object merge(java.util.Map map, Object obj, Object obj2, BiFunction biFunction) {
            return map instanceof Map ? ((Map) map).merge(obj, obj2, biFunction) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$merge((java.util.concurrent.ConcurrentMap) map, obj, obj2, biFunction) : CC.$default$merge(map, obj, obj2, biFunction);
        }

        public static /* synthetic */ Object putIfAbsent(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).putIfAbsent(obj, obj2) : CC.$default$putIfAbsent(map, obj, obj2);
        }

        public static /* synthetic */ boolean remove(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).remove(obj, obj2) : CC.$default$remove(map, obj, obj2);
        }

        public static /* synthetic */ Object replace(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).replace(obj, obj2) : CC.$default$replace(map, obj, obj2);
        }

        public static /* synthetic */ boolean replace(java.util.Map map, Object obj, Object obj2, Object obj3) {
            return map instanceof Map ? ((Map) map).replace(obj, obj2, obj3) : CC.$default$replace(map, obj, obj2, obj3);
        }

        public static /* synthetic */ void replaceAll(java.util.Map map, BiFunction biFunction) {
            if (map instanceof Map) {
                ((Map) map).replaceAll(biFunction);
            } else if (map instanceof java.util.concurrent.ConcurrentMap) {
                ConcurrentMap.CC.$default$replaceAll((java.util.concurrent.ConcurrentMap) map, biFunction);
            } else {
                CC.$default$replaceAll(map, biFunction);
            }
        }
    }

    void clear();

    V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction);

    V computeIfAbsent(K k, Function<? super K, ? extends V> function);

    V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction);

    boolean containsKey(Object obj);

    boolean containsValue(Object obj);

    Set<Map.Entry<K, V>> entrySet();

    boolean equals(Object obj);

    void forEach(BiConsumer<? super K, ? super V> biConsumer);

    V get(Object obj);

    V getOrDefault(Object obj, V v);

    int hashCode();

    boolean isEmpty();

    Set<K> keySet();

    V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction);

    V put(K k, V v);

    void putAll(java.util.Map<? extends K, ? extends V> map);

    V putIfAbsent(K k, V v);

    V remove(Object obj);

    boolean remove(Object obj, Object obj2);

    V replace(K k, V v);

    boolean replace(K k, V v, V v2);

    void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction);

    int size();

    Collection<V> values();

    public interface Entry<K, V> {
        boolean equals(Object obj);

        K getKey();

        V getValue();

        int hashCode();

        V setValue(V v);

        /* renamed from: j$.util.Map$Entry$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K, V>> comparingByKey() {
                return Map$Entry$$ExternalSyntheticLambda2.INSTANCE;
            }

            public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
                return Map$Entry$$ExternalSyntheticLambda3.INSTANCE;
            }

            public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
                cmp.getClass();
                return new Map$Entry$$ExternalSyntheticLambda0(cmp);
            }

            public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
                cmp.getClass();
                return new Map$Entry$$ExternalSyntheticLambda1(cmp);
            }
        }
    }

    /* renamed from: j$.util.Map$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Object $default$getOrDefault(java.util.Map _this, Object key, Object defaultValue) {
            V v = _this.get(key);
            V v2 = v;
            if (v != null || _this.containsKey(key)) {
                return v2;
            }
            return defaultValue;
        }

        public static void $default$forEach(java.util.Map _this, BiConsumer biConsumer) {
            biConsumer.getClass();
            for (Map.Entry<K, V> entry : _this.entrySet()) {
                try {
                    biConsumer.accept(entry.getKey(), entry.getValue());
                } catch (IllegalStateException ise) {
                    throw new ConcurrentModificationException(ise);
                }
            }
        }

        public static void $default$replaceAll(java.util.Map _this, BiFunction biFunction) {
            biFunction.getClass();
            for (Map.Entry<K, V> entry : _this.entrySet()) {
                try {
                    try {
                        entry.setValue(biFunction.apply(entry.getKey(), entry.getValue()));
                    } catch (IllegalStateException ise) {
                        throw new ConcurrentModificationException(ise);
                    }
                } catch (IllegalStateException ise2) {
                    throw new ConcurrentModificationException(ise2);
                }
            }
        }

        public static Object $default$putIfAbsent(java.util.Map _this, Object key, Object value) {
            V v = _this.get(key);
            if (v == null) {
                return _this.put(key, value);
            }
            return v;
        }

        public static boolean $default$remove(java.util.Map _this, Object key, Object value) {
            Object curValue = _this.get(key);
            if (!Objects.equals(curValue, value)) {
                return false;
            }
            if (curValue == null && !_this.containsKey(key)) {
                return false;
            }
            _this.remove(key);
            return true;
        }

        public static boolean $default$replace(java.util.Map _this, Object key, Object oldValue, Object newValue) {
            Object curValue = _this.get(key);
            if (!Objects.equals(curValue, oldValue)) {
                return false;
            }
            if (curValue == null && !_this.containsKey(key)) {
                return false;
            }
            _this.put(key, newValue);
            return true;
        }

        public static Object $default$replace(java.util.Map _this, Object key, Object value) {
            V v = _this.get(key);
            V curValue = v;
            if (v != null || _this.containsKey(key)) {
                return _this.put(key, value);
            }
            return curValue;
        }

        public static Object $default$computeIfAbsent(java.util.Map _this, Object key, Function function) {
            function.getClass();
            V v = _this.get(key);
            V v2 = v;
            if (v == null) {
                V apply = function.apply(key);
                V newValue = apply;
                if (apply != null) {
                    _this.put(key, newValue);
                    return newValue;
                }
            }
            return v2;
        }

        public static Object $default$computeIfPresent(java.util.Map _this, Object key, BiFunction biFunction) {
            biFunction.getClass();
            V v = _this.get(key);
            V oldValue = v;
            if (v == null) {
                return null;
            }
            V newValue = biFunction.apply(key, oldValue);
            if (newValue != null) {
                _this.put(key, newValue);
                return newValue;
            }
            _this.remove(key);
            return null;
        }

        public static Object $default$compute(java.util.Map _this, Object key, BiFunction biFunction) {
            biFunction.getClass();
            V oldValue = _this.get(key);
            V newValue = biFunction.apply(key, oldValue);
            if (newValue != null) {
                _this.put(key, newValue);
                return newValue;
            } else if (oldValue == null && !_this.containsKey(key)) {
                return null;
            } else {
                _this.remove(key);
                return null;
            }
        }

        public static Object $default$merge(java.util.Map _this, Object key, Object value, BiFunction biFunction) {
            Object obj;
            biFunction.getClass();
            value.getClass();
            V oldValue = _this.get(key);
            if (oldValue == null) {
                obj = value;
            } else {
                obj = biFunction.apply(oldValue, value);
            }
            if (obj == null) {
                _this.remove(key);
            } else {
                _this.put(key, obj);
            }
            return obj;
        }
    }
}
