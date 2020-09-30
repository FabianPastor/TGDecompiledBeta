package j$.util;

import j$.util.Map;
import j$.util.concurrent.w;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/* renamed from: j$.util.y  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEy {
    public static /* synthetic */ Object a(Map map, Object obj, BiFunction biFunction) {
        return map instanceof Map ? ((Map) map).compute(obj, biFunction) : map instanceof ConcurrentMap ? w.a((ConcurrentMap) map, obj, biFunction) : Map.CC.$default$compute(map, obj, biFunction);
    }

    public static /* synthetic */ Object b(java.util.Map map, Object obj, Function function) {
        return map instanceof Map ? ((Map) map).computeIfAbsent(obj, function) : map instanceof ConcurrentMap ? w.b((ConcurrentMap) map, obj, function) : Map.CC.$default$computeIfAbsent(map, obj, function);
    }

    public static /* synthetic */ Object c(java.util.Map map, Object obj, BiFunction biFunction) {
        return map instanceof Map ? ((Map) map).computeIfPresent(obj, biFunction) : map instanceof ConcurrentMap ? w.c((ConcurrentMap) map, obj, biFunction) : Map.CC.$default$computeIfPresent(map, obj, biFunction);
    }

    public static /* synthetic */ void d(java.util.Map map, BiConsumer biConsumer) {
        if (map instanceof Map) {
            ((Map) map).forEach(biConsumer);
        } else if (map instanceof ConcurrentMap) {
            w.d((ConcurrentMap) map, biConsumer);
        } else {
            Map.CC.$default$forEach(map, biConsumer);
        }
    }

    public static /* synthetic */ Object e(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).getOrDefault(obj, obj2) : map instanceof ConcurrentMap ? w.e((ConcurrentMap) map, obj, obj2) : Map.CC.$default$getOrDefault(map, obj, obj2);
    }

    public static /* synthetic */ Object f(java.util.Map map, Object obj, Object obj2, BiFunction biFunction) {
        return map instanceof Map ? ((Map) map).merge(obj, obj2, biFunction) : map instanceof ConcurrentMap ? w.f((ConcurrentMap) map, obj, obj2, biFunction) : Map.CC.$default$merge(map, obj, obj2, biFunction);
    }

    public static /* synthetic */ Object g(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).putIfAbsent(obj, obj2) : Map.CC.$default$putIfAbsent(map, obj, obj2);
    }

    public static /* synthetic */ boolean h(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).remove(obj, obj2) : Map.CC.$default$remove(map, obj, obj2);
    }

    public static /* synthetic */ Object i(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).replace(obj, obj2) : Map.CC.$default$replace(map, obj, obj2);
    }

    public static /* synthetic */ boolean j(java.util.Map map, Object obj, Object obj2, Object obj3) {
        return map instanceof Map ? ((Map) map).replace(obj, obj2, obj3) : Map.CC.$default$replace(map, obj, obj2, obj3);
    }

    public static /* synthetic */ void k(java.util.Map map, BiFunction biFunction) {
        if (map instanceof Map) {
            ((Map) map).replaceAll(biFunction);
        } else if (map instanceof ConcurrentMap) {
            w.g((ConcurrentMap) map, biFunction);
        } else {
            Map.CC.$default$replaceAll(map, biFunction);
        }
    }
}
