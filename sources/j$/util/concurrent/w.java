package j$.util.concurrent;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public final /* synthetic */ class w {
    public static Object e(ConcurrentMap _this, Object key, Object defaultValue) {
        V v = _this.get(key);
        return v != null ? v : defaultValue;
    }

    public static void d(ConcurrentMap _this, BiConsumer biConsumer) {
        biConsumer.getClass();
        for (Map.Entry<K, V> entry : _this.entrySet()) {
            try {
                biConsumer.accept(entry.getKey(), entry.getValue());
            } catch (IllegalStateException e) {
            }
        }
    }

    public static void g(ConcurrentMap _this, BiFunction biFunction) {
        biFunction.getClass();
        x.a(_this, new CLASSNAMEa(_this, biFunction));
    }

    public static /* synthetic */ void h(ConcurrentMap _this, BiFunction function, Object k, Object v) {
        while (!_this.replace(k, v, function.a(k, v))) {
            Object obj = _this.get(k);
            v = obj;
            if (obj == null) {
                return;
            }
        }
    }

    public static Object b(ConcurrentMap _this, Object key, Function function) {
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

    public static Object c(ConcurrentMap _this, Object key, BiFunction biFunction) {
        biFunction.getClass();
        while (true) {
            V v = _this.get(key);
            V oldValue = v;
            if (v == null) {
                return oldValue;
            }
            V newValue = biFunction.a(key, oldValue);
            if (newValue != null) {
                if (_this.replace(key, oldValue, newValue)) {
                    return newValue;
                }
            } else if (_this.remove(key, oldValue)) {
                return null;
            }
        }
    }

    public static Object a(ConcurrentMap _this, Object key, BiFunction biFunction) {
        biFunction.getClass();
        V oldValue = _this.get(key);
        while (true) {
            V newValue = biFunction.a(key, oldValue);
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

    public static Object f(ConcurrentMap _this, Object key, Object value, BiFunction biFunction) {
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
                V newValue = biFunction.a(oldValue, value);
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
