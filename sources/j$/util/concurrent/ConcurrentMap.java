package j$.util.concurrent;

import j$.util.Map;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.Map;

public interface ConcurrentMap<K, V> extends Map<K, V> {

    /* renamed from: j$.util.concurrent.ConcurrentMap$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Object compute(java.util.concurrent.ConcurrentMap concurrentMap, Object obj, BiFunction biFunction) {
            return concurrentMap instanceof ConcurrentMap ? ((ConcurrentMap) concurrentMap).compute(obj, biFunction) : CC.$default$compute(concurrentMap, obj, biFunction);
        }

        public static /* synthetic */ Object computeIfAbsent(java.util.concurrent.ConcurrentMap concurrentMap, Object obj, Function function) {
            return concurrentMap instanceof ConcurrentMap ? ((ConcurrentMap) concurrentMap).computeIfAbsent(obj, function) : CC.$default$computeIfAbsent(concurrentMap, obj, function);
        }

        public static /* synthetic */ Object computeIfPresent(java.util.concurrent.ConcurrentMap concurrentMap, Object obj, BiFunction biFunction) {
            return concurrentMap instanceof ConcurrentMap ? ((ConcurrentMap) concurrentMap).computeIfPresent(obj, biFunction) : CC.$default$computeIfPresent(concurrentMap, obj, biFunction);
        }

        public static /* synthetic */ void forEach(java.util.concurrent.ConcurrentMap concurrentMap, BiConsumer biConsumer) {
            if (concurrentMap instanceof ConcurrentMap) {
                ((ConcurrentMap) concurrentMap).forEach(biConsumer);
            } else {
                CC.$default$forEach(concurrentMap, biConsumer);
            }
        }

        public static /* synthetic */ Object getOrDefault(java.util.concurrent.ConcurrentMap concurrentMap, Object obj, Object obj2) {
            return concurrentMap instanceof ConcurrentMap ? ((ConcurrentMap) concurrentMap).getOrDefault(obj, obj2) : CC.$default$getOrDefault(concurrentMap, obj, obj2);
        }

        public static /* synthetic */ Object merge(java.util.concurrent.ConcurrentMap concurrentMap, Object obj, Object obj2, BiFunction biFunction) {
            return concurrentMap instanceof ConcurrentMap ? ((ConcurrentMap) concurrentMap).merge(obj, obj2, biFunction) : CC.$default$merge(concurrentMap, obj, obj2, biFunction);
        }

        public static /* synthetic */ void replaceAll(java.util.concurrent.ConcurrentMap concurrentMap, BiFunction biFunction) {
            if (concurrentMap instanceof ConcurrentMap) {
                ((ConcurrentMap) concurrentMap).replaceAll(biFunction);
            } else {
                CC.$default$replaceAll(concurrentMap, biFunction);
            }
        }
    }

    V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction);

    V computeIfAbsent(K k, Function<? super K, ? extends V> function);

    V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction);

    void forEach(BiConsumer<? super K, ? super V> biConsumer);

    V getOrDefault(Object obj, V v);

    V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction);

    V putIfAbsent(K k, V v);

    boolean remove(Object obj, Object obj2);

    V replace(K k, V v);

    boolean replace(K k, V v, V v2);

    void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction);

    /* renamed from: j$.util.concurrent.ConcurrentMap$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Object $default$getOrDefault(java.util.concurrent.ConcurrentMap _this, Object key, Object defaultValue) {
            V v = _this.get(key);
            return v != null ? v : defaultValue;
        }

        public static void $default$forEach(java.util.concurrent.ConcurrentMap _this, BiConsumer biConsumer) {
            biConsumer.getClass();
            for (Map.Entry<K, V> entry : _this.entrySet()) {
                try {
                    biConsumer.accept(entry.getKey(), entry.getValue());
                } catch (IllegalStateException e) {
                }
            }
        }

        public static void $default$replaceAll(java.util.concurrent.ConcurrentMap _this, BiFunction biFunction) {
            biFunction.getClass();
            EL.forEach(_this, new ConcurrentMap$$ExternalSyntheticLambda0(_this, biFunction));
        }

        public static /* synthetic */ void lambda$replaceAll$0(java.util.concurrent.ConcurrentMap _this, BiFunction function, Object k, Object v) {
            while (!_this.replace(k, v, function.apply(k, v))) {
                Object obj = _this.get(k);
                v = obj;
                if (obj == null) {
                    return;
                }
            }
        }

        public static Object $default$computeIfAbsent(java.util.concurrent.ConcurrentMap _this, Object key, Function function) {
            function.getClass();
            V v = _this.get(key);
            V v2 = v;
            if (v == null) {
                V apply = function.apply(key);
                V newValue = apply;
                if (apply != null) {
                    V putIfAbsent = _this.putIfAbsent(key, newValue);
                    v2 = putIfAbsent;
                    if (putIfAbsent == null) {
                        return newValue;
                    }
                }
            }
            return v2;
        }

        public static Object $default$computeIfPresent(java.util.concurrent.ConcurrentMap _this, Object key, BiFunction biFunction) {
            biFunction.getClass();
            while (true) {
                V v = _this.get(key);
                V oldValue = v;
                if (v == null) {
                    return oldValue;
                }
                V newValue = biFunction.apply(key, oldValue);
                if (newValue != null) {
                    if (_this.replace(key, oldValue, newValue)) {
                        return newValue;
                    }
                } else if (_this.remove(key, oldValue)) {
                    return null;
                }
            }
        }

        public static Object $default$compute(java.util.concurrent.ConcurrentMap _this, Object key, BiFunction biFunction) {
            biFunction.getClass();
            V oldValue = _this.get(key);
            while (true) {
                V newValue = biFunction.apply(key, oldValue);
                if (newValue == null) {
                    if ((oldValue == null && !_this.containsKey(key)) || _this.remove(key, oldValue)) {
                        return null;
                    }
                    oldValue = _this.get(key);
                } else if (oldValue == null) {
                    V putIfAbsent = _this.putIfAbsent(key, newValue);
                    oldValue = putIfAbsent;
                    if (putIfAbsent == null) {
                        return newValue;
                    }
                } else if (_this.replace(key, oldValue, newValue)) {
                    return newValue;
                } else {
                    oldValue = _this.get(key);
                }
            }
        }

        public static Object $default$merge(java.util.concurrent.ConcurrentMap _this, Object key, Object value, BiFunction biFunction) {
            biFunction.getClass();
            value.getClass();
            V oldValue = _this.get(key);
            while (true) {
                if (oldValue == null) {
                    V putIfAbsent = _this.putIfAbsent(key, value);
                    oldValue = putIfAbsent;
                    if (putIfAbsent == null) {
                        return value;
                    }
                } else {
                    V newValue = biFunction.apply(oldValue, value);
                    if (newValue != null) {
                        if (_this.replace(key, oldValue, newValue)) {
                            return newValue;
                        }
                    } else if (_this.remove(key, oldValue)) {
                        return null;
                    }
                    oldValue = _this.get(key);
                }
            }
        }
    }
}
