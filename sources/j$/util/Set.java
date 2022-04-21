package j$.util;

import j$.util.SortedSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.SortedSet;

public interface Set<E> extends Collection<E> {

    /* renamed from: j$.util.Set$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Spliterator spliterator(java.util.Set set) {
            return set instanceof Set ? ((Set) set).spliterator() : set instanceof LinkedHashSet ? DesugarLinkedHashSet.spliterator((LinkedHashSet) set) : set instanceof SortedSet ? SortedSet.CC.$default$spliterator((java.util.SortedSet) set) : CC.$default$spliterator(set);
        }
    }

    boolean add(E e);

    boolean addAll(Collection<? extends E> collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(Collection<?> collection);

    boolean equals(Object obj);

    int hashCode();

    boolean isEmpty();

    Iterator<E> iterator();

    boolean remove(Object obj);

    boolean removeAll(Collection<?> collection);

    boolean retainAll(Collection<?> collection);

    int size();

    Spliterator<E> spliterator();

    Object[] toArray();

    <T> T[] toArray(T[] tArr);

    /* renamed from: j$.util.Set$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Spliterator $default$spliterator(java.util.Set _this) {
            return Spliterators.spliterator(_this, 1);
        }
    }
}
