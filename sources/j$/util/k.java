package j$.util;

import j$.util.Collection;
import j$.util.Comparator;
import j$.util.List;
import j$.util.Map;
import j$.util.Set;
import j$.util.concurrent.a;
import j$.util.concurrent.b;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.v;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

public final /* synthetic */ class k {
    public static void A(Map map, BiFunction biFunction) {
        if (map instanceof Map) {
            ((Map) map).replaceAll(biFunction);
        } else if (map instanceof ConcurrentMap) {
            ConcurrentMap concurrentMap = (ConcurrentMap) map;
            biFunction.getClass();
            a aVar = new a(concurrentMap, biFunction);
            if (concurrentMap instanceof b) {
                ((b) concurrentMap).forEach(aVar);
            } else {
                d(concurrentMap, aVar);
            }
        } else {
            Map.CC.$default$replaceAll(map, biFunction);
        }
    }

    public static /* synthetic */ void B(List list, Comparator comparator) {
        if (list instanceof List) {
            ((List) list).sort(comparator);
        } else {
            List.CC.$default$sort(list, comparator);
        }
    }

    public static Spliterator C(Collection collection) {
        if (collection instanceof Collection) {
            return ((Collection) collection).spliterator();
        }
        if (collection instanceof LinkedHashSet) {
            LinkedHashSet linkedHashSet = (LinkedHashSet) collection;
            linkedHashSet.getClass();
            return new v.i((Collection) linkedHashSet, 17);
        } else if (collection instanceof SortedSet) {
            SortedSet sortedSet = (SortedSet) collection;
            return new u(sortedSet, sortedSet, 21);
        } else if (collection instanceof java.util.List) {
            return List.CC.$default$spliterator((java.util.List) collection);
        } else {
            return collection instanceof Set ? Set.CC.$default$spliterator((java.util.Set) collection) : Collection.CC.$default$spliterator(collection);
        }
    }

    public static /* synthetic */ Comparator D(Comparator comparator, Comparator comparator2) {
        return comparator instanceof Comparator ? ((Comparator) comparator).thenComparing(comparator2) : Comparator.CC.$default$thenComparing(comparator, comparator2);
    }

    public static void a(A2 a2) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void b(A2 a2) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void c(A2 a2) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void d(ConcurrentMap concurrentMap, BiConsumer biConsumer) {
        biConsumer.getClass();
        for (Map.Entry entry : concurrentMap.entrySet()) {
            try {
                biConsumer.accept(entry.getKey(), entry.getValue());
            } catch (IllegalStateException unused) {
            }
        }
    }

    public static long e(Spliterator spliterator) {
        if ((spliterator.characteristics() & 64) == 0) {
            return -1;
        }
        return spliterator.estimateSize();
    }

    public static boolean f(Spliterator spliterator, int i) {
        return (spliterator.characteristics() & i) == i;
    }

    public static Object g(java.util.Map map, Object obj, BiFunction biFunction) {
        Object apply;
        if (map instanceof Map) {
            return ((Map) map).compute(obj, biFunction);
        }
        if (!(map instanceof ConcurrentMap)) {
            return Map.CC.$default$compute(map, obj, biFunction);
        }
        ConcurrentMap concurrentMap = (ConcurrentMap) map;
        biFunction.getClass();
        loop0:
        while (true) {
            Object obj2 = concurrentMap.get(obj);
            while (true) {
                apply = biFunction.apply(obj, obj2);
                if (apply != null) {
                    if (obj2 == null) {
                        obj2 = concurrentMap.putIfAbsent(obj, apply);
                        if (obj2 == null) {
                            break loop0;
                        }
                    } else if (concurrentMap.replace(obj, obj2, apply)) {
                        break;
                    }
                } else {
                    apply = null;
                    if ((obj2 == null && !concurrentMap.containsKey(obj)) || concurrentMap.remove(obj, obj2)) {
                        break;
                    }
                }
            }
        }
        return apply;
    }

    public static Object h(java.util.Map map, Object obj, Function function) {
        Object apply;
        if (map instanceof Map) {
            return ((Map) map).computeIfAbsent(obj, function);
        }
        if (!(map instanceof ConcurrentMap)) {
            return Map.CC.$default$computeIfAbsent(map, obj, function);
        }
        ConcurrentMap concurrentMap = (ConcurrentMap) map;
        function.getClass();
        Object obj2 = concurrentMap.get(obj);
        if (obj2 != null || (apply = function.apply(obj)) == null) {
            return obj2;
        }
        Object putIfAbsent = concurrentMap.putIfAbsent(obj, apply);
        return putIfAbsent == null ? apply : putIfAbsent;
    }

    public static Object i(java.util.Map map, Object obj, BiFunction biFunction) {
        if (map instanceof Map) {
            return ((Map) map).computeIfPresent(obj, biFunction);
        }
        if (!(map instanceof ConcurrentMap)) {
            return Map.CC.$default$computeIfPresent(map, obj, biFunction);
        }
        ConcurrentMap concurrentMap = (ConcurrentMap) map;
        biFunction.getClass();
        while (true) {
            Object obj2 = concurrentMap.get(obj);
            if (obj2 == null) {
                return obj2;
            }
            Object apply = biFunction.apply(obj, obj2);
            if (apply != null) {
                if (concurrentMap.replace(obj, obj2, apply)) {
                    return apply;
                }
            } else if (concurrentMap.remove(obj, obj2)) {
                return null;
            }
        }
    }

    public static Optional j(Optional optional) {
        if (optional == null) {
            return null;
        }
        return optional.isPresent() ? Optional.of(optional.get()) : Optional.empty();
    }

    public static p k(OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        return optionalDouble.isPresent() ? p.d(optionalDouble.getAsDouble()) : p.a();
    }

    public static q l(OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        return optionalInt.isPresent() ? q.d(optionalInt.getAsInt()) : q.a();
    }

    public static r m(OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        return optionalLong.isPresent() ? r.d(optionalLong.getAsLong()) : r.a();
    }

    public static Optional n(Optional optional) {
        if (optional == null) {
            return null;
        }
        return optional.isPresent() ? Optional.of(optional.get()) : Optional.empty();
    }

    public static OptionalDouble o(p pVar) {
        if (pVar == null) {
            return null;
        }
        return pVar.c() ? OptionalDouble.of(pVar.b()) : OptionalDouble.empty();
    }

    public static OptionalInt p(q qVar) {
        if (qVar == null) {
            return null;
        }
        return qVar.c() ? OptionalInt.of(qVar.b()) : OptionalInt.empty();
    }

    public static OptionalLong q(r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar.c() ? OptionalLong.of(rVar.b()) : OptionalLong.empty();
    }

    public static boolean r(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static void s(java.util.Collection collection, Consumer consumer) {
        if (collection instanceof Collection) {
            ((Collection) collection).forEach(consumer);
            return;
        }
        consumer.getClass();
        for (Object accept : collection) {
            consumer.accept(accept);
        }
    }

    public static /* synthetic */ void t(java.util.Map map, BiConsumer biConsumer) {
        if (map instanceof Map) {
            ((Map) map).forEach(biConsumer);
        } else if (map instanceof ConcurrentMap) {
            d((ConcurrentMap) map, biConsumer);
        } else {
            Map.CC.$default$forEach(map, biConsumer);
        }
    }

    public static Object u(java.util.Map map, Object obj, Object obj2) {
        if (map instanceof Map) {
            return ((Map) map).getOrDefault(obj, obj2);
        }
        if (!(map instanceof ConcurrentMap)) {
            return Map.CC.$default$getOrDefault(map, obj, obj2);
        }
        Object obj3 = ((ConcurrentMap) map).get(obj);
        return obj3 != null ? obj3 : obj2;
    }

    public static Object v(java.util.Map map, Object obj, Object obj2, BiFunction biFunction) {
        if (map instanceof Map) {
            return ((Map) map).merge(obj, obj2, biFunction);
        }
        if (!(map instanceof ConcurrentMap)) {
            return Map.CC.$default$merge(map, obj, obj2, biFunction);
        }
        ConcurrentMap concurrentMap = (ConcurrentMap) map;
        biFunction.getClass();
        obj2.getClass();
        while (true) {
            Object obj3 = concurrentMap.get(obj);
            while (obj3 == null) {
                obj3 = concurrentMap.putIfAbsent(obj, obj2);
                if (obj3 == null) {
                    return obj2;
                }
            }
            Object apply = biFunction.apply(obj3, obj2);
            if (apply != null) {
                if (concurrentMap.replace(obj, obj3, apply)) {
                    return apply;
                }
            } else if (concurrentMap.remove(obj, obj3)) {
                return null;
            }
        }
    }

    public static /* synthetic */ Object w(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).putIfAbsent(obj, obj2) : Map.CC.$default$putIfAbsent(map, obj, obj2);
    }

    public static /* synthetic */ boolean x(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).remove(obj, obj2) : Map.CC.$default$remove(map, obj, obj2);
    }

    public static /* synthetic */ Object y(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).replace(obj, obj2) : Map.CC.$default$replace(map, obj, obj2);
    }

    public static /* synthetic */ boolean z(java.util.Map map, Object obj, Object obj2, Object obj3) {
        return map instanceof Map ? ((Map) map).replace(obj, obj2, obj3) : Map.CC.$default$replace(map, obj, obj2, obj3);
    }
}
