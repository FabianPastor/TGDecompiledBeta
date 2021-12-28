package j$.util;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/* renamed from: j$.util.Collection$-EL  reason: invalid class name */
public final /* synthetic */ class Collection$EL {
    public static /* synthetic */ void a(Collection collection, Consumer consumer) {
        if (collection instanceof CLASSNAMEb) {
            ((CLASSNAMEb) collection).forEach(consumer);
        } else {
            CLASSNAMEa.a(collection, consumer);
        }
    }

    public static y b(Collection collection) {
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

    public static /* synthetic */ boolean removeIf(Collection collection, Predicate predicate) {
        return collection instanceof CLASSNAMEb ? ((CLASSNAMEb) collection).k(predicate) : CLASSNAMEa.h(collection, predicate);
    }
}
