package j$.util;

import j$.CLASSNAMEe;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;

public interface Map {

    public interface Entry {
        boolean equals(Object obj);

        Object getKey();

        Object getValue();

        int hashCode();

        Object setValue(Object obj);
    }

    void clear();

    Object compute(Object obj, BiFunction biFunction);

    Object computeIfAbsent(Object obj, Function function);

    Object computeIfPresent(Object obj, BiFunction biFunction);

    boolean containsKey(Object obj);

    boolean containsValue(Object obj);

    Set entrySet();

    boolean equals(Object obj);

    void forEach(BiConsumer biConsumer);

    Object get(Object obj);

    Object getOrDefault(Object obj, Object obj2);

    int hashCode();

    boolean isEmpty();

    Set keySet();

    Object merge(Object obj, Object obj2, BiFunction biFunction);

    Object put(Object obj, Object obj2);

    void putAll(java.util.Map map);

    Object putIfAbsent(Object obj, Object obj2);

    Object remove(Object obj);

    boolean remove(Object obj, Object obj2);

    Object replace(Object obj, Object obj2);

    boolean replace(Object obj, Object obj2, Object obj3);

    void replaceAll(BiFunction biFunction);

    int size();

    Collection values();

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
                        entry.setValue(biFunction.a(entry.getKey(), entry.getValue()));
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
            if (!CLASSNAMEe.a(curValue, value)) {
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
            if (!CLASSNAMEe.a(curValue, oldValue)) {
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
            V newValue = biFunction.a(key, oldValue);
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
            V newValue = biFunction.a(key, oldValue);
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
                obj = biFunction.a(oldValue, value);
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
