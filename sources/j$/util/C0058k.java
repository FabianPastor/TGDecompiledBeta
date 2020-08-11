package j$.util;

import j$.util.Collection;
import j$.util.List;
import j$.util.Set;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.stream.Stream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/* renamed from: j$.util.k  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk {
    public static /* synthetic */ void a(Collection collection, Consumer consumer) {
        if (collection instanceof Collection) {
            ((Collection) collection).forEach(consumer);
        } else {
            Collection.CC.a(collection, consumer);
        }
    }

    public static /* synthetic */ boolean b(java.util.Collection collection, Predicate predicate) {
        return collection instanceof Collection ? ((Collection) collection).removeIf(predicate) : Collection.CC.$default$removeIf(collection, predicate);
    }

    public static /* synthetic */ Spliterator c(java.util.Collection collection) {
        return collection instanceof Collection ? ((Collection) collection).spliterator() : collection instanceof LinkedHashSet ? CLASSNAMEp.a((LinkedHashSet) collection) : collection instanceof List ? List.CC.$default$spliterator((java.util.List) collection) : collection instanceof SortedSet ? L.a((SortedSet) collection) : collection instanceof Set ? Set.CC.$default$spliterator((java.util.Set) collection) : Collection.CC.$default$spliterator(collection);
    }

    public static /* synthetic */ Stream d(java.util.Collection collection) {
        return collection instanceof Collection ? ((Collection) collection).stream() : Collection.CC.$default$stream(collection);
    }
}
