package j$.util;

import j$.util.Map;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.stream.AbstractCLASSNAMEo1;
import j$.util.stream.Stream;
import j$.util.u;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentMap;
/* renamed from: j$.util.a  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public abstract /* synthetic */ class AbstractCLASSNAMEa {
    public static Object A(java.util.Map map, Object obj, Object obj2, BiFunction biFunction) {
        if (map instanceof Map) {
            return ((Map) map).merge(obj, obj2, biFunction);
        }
        if (map instanceof ConcurrentMap) {
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
        } else {
            return Map.CC.$default$merge(map, obj, obj2, biFunction);
        }
    }

    public static /* synthetic */ Object B(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).putIfAbsent(obj, obj2) : map.get(obj);
    }

    public static /* synthetic */ boolean C(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).remove(obj, obj2) : Map.CC.$default$remove(map, obj, obj2);
    }

    public static /* synthetic */ Object D(java.util.Map map, Object obj, Object obj2) {
        return map instanceof Map ? ((Map) map).replace(obj, obj2) : map.get(obj);
    }

    public static /* synthetic */ boolean E(java.util.Map map, Object obj, Object obj2, Object obj3) {
        return map instanceof Map ? ((Map) map).replace(obj, obj2, obj3) : Map.CC.$default$replace(map, obj, obj2, obj3);
    }

    public static void F(java.util.Map map, BiFunction biFunction) {
        if (map instanceof Map) {
            ((Map) map).replaceAll(biFunction);
        } else if (map instanceof ConcurrentMap) {
            ConcurrentMap concurrentMap = (ConcurrentMap) map;
            biFunction.getClass();
            j$.util.concurrent.a aVar = new j$.util.concurrent.a(concurrentMap, biFunction);
            if (concurrentMap instanceof j$.util.concurrent.b) {
                ((j$.util.concurrent.b) concurrentMap).forEach(aVar);
            } else {
                j$.lang.d.a(concurrentMap, aVar);
            }
        } else {
            Map.CC.$default$replaceAll(map, biFunction);
        }
    }

    public static void G(List list, Comparator comparator) {
        if (DesugarCollections.b.isInstance(list)) {
            DesugarCollections.d(list, comparator);
            return;
        }
        Object[] array = list.toArray();
        Arrays.sort(array, comparator);
        ListIterator listIterator = list.listIterator();
        for (Object obj : array) {
            listIterator.next();
            listIterator.set(obj);
        }
    }

    public static Comparator H(Comparator comparator, Comparator comparator2) {
        if (comparator instanceof InterfaceCLASSNAMEe) {
            return ((EnumCLASSNAMEf) ((InterfaceCLASSNAMEe) comparator)).thenComparing(comparator2);
        }
        comparator2.getClass();
        return new CLASSNAMEc(comparator, comparator2);
    }

    public static void a(Collection collection, Consumer consumer) {
        consumer.getClass();
        for (Object obj : collection) {
            consumer.accept(obj);
        }
    }

    public static void b(t tVar, Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            tVar.e((j$.util.function.f) consumer);
        } else if (N.a) {
            N.a(tVar.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        } else {
            consumer.getClass();
            tVar.e(new CLASSNAMEm(consumer));
        }
    }

    public static void c(u.a aVar, Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            aVar.c((j$.util.function.l) consumer);
        } else if (N.a) {
            N.a(aVar.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        } else {
            consumer.getClass();
            aVar.c(new o(consumer));
        }
    }

    public static void d(v vVar, Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            vVar.d((j$.util.function.q) consumer);
        } else if (N.a) {
            N.a(vVar.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        } else {
            consumer.getClass();
            vVar.d(new q(consumer));
        }
    }

    public static long e(u uVar) {
        if ((uVar.characteristics() & 64) == 0) {
            return -1L;
        }
        return uVar.estimateSize();
    }

    public static boolean f(u uVar, int i) {
        return (uVar.characteristics() & i) == i;
    }

    public static Stream g(Collection collection) {
        return AbstractCLASSNAMEo1.y(Collection$EL.b(collection), true);
    }

    public static boolean h(Collection collection, Predicate predicate) {
        if (DesugarCollections.a.isInstance(collection)) {
            return DesugarCollections.c(collection, predicate);
        }
        predicate.getClass();
        boolean z = false;
        java.util.Iterator it = collection.iterator();
        while (it.hasNext()) {
            if (predicate.test(it.next())) {
                it.remove();
                z = true;
            }
        }
        return z;
    }

    public static Stream i(Collection collection) {
        return AbstractCLASSNAMEo1.y(Collection$EL.b(collection), false);
    }

    public static boolean j(t tVar, Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            return tVar.k((j$.util.function.f) consumer);
        }
        if (!N.a) {
            consumer.getClass();
            return tVar.k(new CLASSNAMEm(consumer));
        }
        N.a(tVar.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
        throw null;
    }

    public static boolean k(u.a aVar, Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            return aVar.g((j$.util.function.l) consumer);
        }
        if (!N.a) {
            consumer.getClass();
            return aVar.g(new o(consumer));
        }
        N.a(aVar.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
        throw null;
    }

    public static boolean l(v vVar, Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            return vVar.i((j$.util.function.q) consumer);
        }
        if (!N.a) {
            consumer.getClass();
            return vVar.i(new q(consumer));
        }
        N.a(vVar.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
        throw null;
    }

    public static Object m(java.util.Map map, Object obj, BiFunction biFunction) {
        Object apply;
        if (map instanceof Map) {
            return ((Map) map).compute(obj, biFunction);
        }
        if (map instanceof ConcurrentMap) {
            ConcurrentMap concurrentMap = (ConcurrentMap) map;
            biFunction.getClass();
            loop0: while (true) {
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
        return Map.CC.$default$compute(map, obj, biFunction);
    }

    public static Object n(java.util.Map map, Object obj, Function function) {
        Object apply;
        if (map instanceof Map) {
            return ((Map) map).computeIfAbsent(obj, function);
        }
        if (map instanceof ConcurrentMap) {
            ConcurrentMap concurrentMap = (ConcurrentMap) map;
            function.getClass();
            Object obj2 = concurrentMap.get(obj);
            return (obj2 == null && (apply = function.apply(obj)) != null && (obj2 = concurrentMap.putIfAbsent(obj, apply)) == null) ? apply : obj2;
        }
        return Map.CC.$default$computeIfAbsent(map, obj, function);
    }

    public static Object o(java.util.Map map, Object obj, BiFunction biFunction) {
        if (map instanceof Map) {
            return ((Map) map).computeIfPresent(obj, biFunction);
        }
        if (map instanceof ConcurrentMap) {
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
        } else {
            return Map.CC.$default$computeIfPresent(map, obj, biFunction);
        }
    }

    public static Optional p(java.util.Optional optional) {
        if (optional == null) {
            return null;
        }
        return optional.isPresent() ? Optional.of(optional.get()) : Optional.empty();
    }

    public static CLASSNAMEj q(OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        return optionalDouble.isPresent() ? CLASSNAMEj.d(optionalDouble.getAsDouble()) : CLASSNAMEj.a();
    }

    public static CLASSNAMEk r(OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        return optionalInt.isPresent() ? CLASSNAMEk.d(optionalInt.getAsInt()) : CLASSNAMEk.a();
    }

    public static CLASSNAMEl s(OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        return optionalLong.isPresent() ? CLASSNAMEl.d(optionalLong.getAsLong()) : CLASSNAMEl.a();
    }

    public static java.util.Optional t(Optional optional) {
        if (optional == null) {
            return null;
        }
        return optional.isPresent() ? java.util.Optional.of(optional.get()) : java.util.Optional.empty();
    }

    public static OptionalDouble u(CLASSNAMEj CLASSNAMEj) {
        if (CLASSNAMEj == null) {
            return null;
        }
        return CLASSNAMEj.c() ? OptionalDouble.of(CLASSNAMEj.b()) : OptionalDouble.empty();
    }

    public static OptionalInt v(CLASSNAMEk CLASSNAMEk) {
        if (CLASSNAMEk == null) {
            return null;
        }
        return CLASSNAMEk.c() ? OptionalInt.of(CLASSNAMEk.b()) : OptionalInt.empty();
    }

    public static OptionalLong w(CLASSNAMEl CLASSNAMEl) {
        if (CLASSNAMEl == null) {
            return null;
        }
        return CLASSNAMEl.c() ? OptionalLong.of(CLASSNAMEl.b()) : OptionalLong.empty();
    }

    public static boolean x(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static /* synthetic */ void y(java.util.Map map, BiConsumer biConsumer) {
        if (map instanceof Map) {
            ((Map) map).forEach(biConsumer);
        } else if (map instanceof ConcurrentMap) {
            j$.lang.d.a((ConcurrentMap) map, biConsumer);
        } else {
            Map.CC.$default$forEach(map, biConsumer);
        }
    }

    public static Object z(java.util.Map map, Object obj, Object obj2) {
        if (map instanceof Map) {
            return ((Map) map).getOrDefault(obj, obj2);
        }
        if (map instanceof ConcurrentMap) {
            Object obj3 = ((ConcurrentMap) map).get(obj);
            return obj3 != null ? obj3 : obj2;
        }
        Object obj4 = map.get(obj);
        return (obj4 != null || map.containsKey(obj)) ? obj4 : obj2;
    }
}
