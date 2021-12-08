package j$.util;

import j$.lang.d;
import j$.util.Map;
import j$.util.concurrent.a;
import j$.util.concurrent.b;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.function.l;
import j$.util.function.q;
import j$.util.function.y;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.Stream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

/* renamed from: j$.util.a  reason: case insensitive filesystem */
public abstract /* synthetic */ class CLASSNAMEa {
    public static Object A(Map map, Object obj, Object obj2) {
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

    public static Object B(Map map, Object obj, Object obj2, BiFunction biFunction) {
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

    public static Object C(java.util.Map map, Object obj, Object obj2) {
        if (map instanceof Map) {
            return ((Map) map).putIfAbsent(obj, obj2);
        }
        Object obj3 = map.get(obj);
        return obj3 == null ? map.put(obj, obj2) : obj3;
    }

    public static boolean D(java.util.Map map, Object obj, Object obj2) {
        if (map instanceof Map) {
            return ((Map) map).remove(obj, obj2);
        }
        Object obj3 = map.get(obj);
        if (!x(obj3, obj2) || (obj3 == null && !map.containsKey(obj))) {
            return false;
        }
        map.remove(obj);
        return true;
    }

    public static Object E(java.util.Map map, Object obj, Object obj2) {
        if (map instanceof Map) {
            return ((Map) map).replace(obj, obj2);
        }
        Object obj3 = map.get(obj);
        return (obj3 != null || map.containsKey(obj)) ? map.put(obj, obj2) : obj3;
    }

    public static boolean F(java.util.Map map, Object obj, Object obj2, Object obj3) {
        if (map instanceof Map) {
            return ((Map) map).replace(obj, obj2, obj3);
        }
        Object obj4 = map.get(obj);
        if (!x(obj4, obj2) || (obj4 == null && !map.containsKey(obj))) {
            return false;
        }
        map.put(obj, obj3);
        return true;
    }

    public static void G(java.util.Map map, BiFunction biFunction) {
        if (map instanceof Map) {
            ((Map) map).replaceAll(biFunction);
        } else if (map instanceof ConcurrentMap) {
            ConcurrentMap concurrentMap = (ConcurrentMap) map;
            biFunction.getClass();
            a aVar = new a(concurrentMap, biFunction);
            if (concurrentMap instanceof b) {
                ((b) concurrentMap).forEach(aVar);
            } else {
                d.a(concurrentMap, aVar);
            }
        } else {
            Map.CC.$default$replaceAll(map, biFunction);
        }
    }

    public static void H(List list, Comparator comparator) {
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

    public static y I(Collection collection) {
        if (collection instanceof CLASSNAMEb) {
            return ((CLASSNAMEb) collection).spliterator();
        }
        if (collection instanceof LinkedHashSet) {
            LinkedHashSet linkedHashSet = (LinkedHashSet) collection;
            linkedHashSet.getClass();
            return new L(linkedHashSet, 17);
        } else if (collection instanceof SortedSet) {
            SortedSet sortedSet = (SortedSet) collection;
            return new t(sortedSet, sortedSet, 21);
        } else if (collection instanceof Set) {
            Set set = (Set) collection;
            set.getClass();
            return new L(set, 1);
        } else if (collection instanceof List) {
            List list = (List) collection;
            list.getClass();
            return new L(list, 16);
        } else {
            collection.getClass();
            return new L(collection, 0);
        }
    }

    public static Comparator J(Comparator comparator, Comparator comparator2) {
        if (comparator instanceof CLASSNAMEe) {
            return ((CLASSNAMEf) ((CLASSNAMEe) comparator)).thenComparing(comparator2);
        }
        comparator2.getClass();
        return new CLASSNAMEc(comparator, comparator2);
    }

    public static void a(Collection collection, Consumer consumer) {
        consumer.getClass();
        for (Object accept : collection) {
            consumer.accept(accept);
        }
    }

    public static void b(u uVar, Consumer consumer) {
        if (consumer instanceof f) {
            uVar.e((f) consumer);
        } else if (!P.a) {
            consumer.getClass();
            uVar.e(new CLASSNAMEm(consumer));
        } else {
            P.a(uVar.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            throw null;
        }
    }

    public static void c(v vVar, Consumer consumer) {
        if (consumer instanceof l) {
            vVar.c((l) consumer);
        } else if (!P.a) {
            consumer.getClass();
            vVar.c(new CLASSNAMEo(consumer));
        } else {
            P.a(vVar.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            throw null;
        }
    }

    public static void d(w wVar, Consumer consumer) {
        if (consumer instanceof q) {
            wVar.d((q) consumer);
        } else if (!P.a) {
            consumer.getClass();
            wVar.d(new q(consumer));
        } else {
            P.a(wVar.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            throw null;
        }
    }

    public static long e(y yVar) {
        if ((yVar.characteristics() & 64) == 0) {
            return -1;
        }
        return yVar.estimateSize();
    }

    public static boolean f(y yVar, int i) {
        return (yVar.characteristics() & i) == i;
    }

    public static Stream g(Collection collection) {
        return CLASSNAMEp1.y(I(collection), true);
    }

    public static boolean h(Collection collection, y yVar) {
        if (DesugarCollections.a.isInstance(collection)) {
            return DesugarCollections.c(collection, yVar);
        }
        yVar.getClass();
        boolean z = false;
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            if (yVar.test(it.next())) {
                it.remove();
                z = true;
            }
        }
        return z;
    }

    public static Stream i(Collection collection) {
        return CLASSNAMEp1.y(I(collection), false);
    }

    public static boolean j(u uVar, Consumer consumer) {
        if (consumer instanceof f) {
            return uVar.k((f) consumer);
        }
        if (!P.a) {
            consumer.getClass();
            return uVar.k(new CLASSNAMEm(consumer));
        }
        P.a(uVar.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
        throw null;
    }

    public static boolean k(v vVar, Consumer consumer) {
        if (consumer instanceof l) {
            return vVar.g((l) consumer);
        }
        if (!P.a) {
            consumer.getClass();
            return vVar.g(new CLASSNAMEo(consumer));
        }
        P.a(vVar.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
        throw null;
    }

    public static boolean l(w wVar, Consumer consumer) {
        if (consumer instanceof q) {
            return wVar.i((q) consumer);
        }
        if (!P.a) {
            consumer.getClass();
            return wVar.i(new q(consumer));
        }
        P.a(wVar.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
        throw null;
    }

    public static Object m(java.util.Map map, Object obj, BiFunction biFunction) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0020, code lost:
        r0 = r1.putIfAbsent(r2, (r3 = r3.apply(r2)));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Object n(java.util.Map r1, java.lang.Object r2, j$.util.function.Function r3) {
        /*
            boolean r0 = r1 instanceof j$.util.Map
            if (r0 == 0) goto L_0x000b
            j$.util.Map r1 = (j$.util.Map) r1
            java.lang.Object r1 = r1.computeIfAbsent(r2, r3)
            return r1
        L_0x000b:
            boolean r0 = r1 instanceof java.util.concurrent.ConcurrentMap
            if (r0 == 0) goto L_0x0029
            java.util.concurrent.ConcurrentMap r1 = (java.util.concurrent.ConcurrentMap) r1
            r3.getClass()
            java.lang.Object r0 = r1.get(r2)
            if (r0 != 0) goto L_0x0027
            java.lang.Object r3 = r3.apply(r2)
            if (r3 == 0) goto L_0x0027
            java.lang.Object r0 = r1.putIfAbsent(r2, r3)
            if (r0 != 0) goto L_0x0027
            goto L_0x0028
        L_0x0027:
            r3 = r0
        L_0x0028:
            return r3
        L_0x0029:
            java.lang.Object r1 = j$.util.Map.CC.$default$computeIfAbsent(r1, r2, r3)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.CLASSNAMEa.n(java.util.Map, java.lang.Object, j$.util.function.Function):java.lang.Object");
    }

    public static Object o(java.util.Map map, Object obj, BiFunction biFunction) {
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

    public static Optional p(Optional optional) {
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

    public static Optional t(Optional optional) {
        if (optional == null) {
            return null;
        }
        return optional.isPresent() ? Optional.of(optional.get()) : Optional.empty();
    }

    public static OptionalDouble u(CLASSNAMEj jVar) {
        if (jVar == null) {
            return null;
        }
        return jVar.c() ? OptionalDouble.of(jVar.b()) : OptionalDouble.empty();
    }

    public static OptionalInt v(CLASSNAMEk kVar) {
        if (kVar == null) {
            return null;
        }
        return kVar.c() ? OptionalInt.of(kVar.b()) : OptionalInt.empty();
    }

    public static OptionalLong w(CLASSNAMEl lVar) {
        if (lVar == null) {
            return null;
        }
        return lVar.c() ? OptionalLong.of(lVar.b()) : OptionalLong.empty();
    }

    public static boolean x(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static /* synthetic */ void y(Collection collection, Consumer consumer) {
        if (collection instanceof CLASSNAMEb) {
            ((CLASSNAMEb) collection).forEach(consumer);
        } else {
            a(collection, consumer);
        }
    }

    public static /* synthetic */ void z(java.util.Map map, BiConsumer biConsumer) {
        if (map instanceof Map) {
            ((Map) map).forEach(biConsumer);
        } else if (map instanceof ConcurrentMap) {
            d.a((ConcurrentMap) map, biConsumer);
        } else {
            Map.CC.$default$forEach(map, biConsumer);
        }
    }
}
