package j$.util;

import java.util.Collection;
import java.util.Iterator;

public interface Set extends Collection, Collection {
    boolean add(Object obj);

    boolean addAll(Collection collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(Collection collection);

    boolean equals(Object obj);

    int hashCode();

    boolean isEmpty();

    Iterator iterator();

    boolean remove(Object obj);

    boolean removeAll(Collection collection);

    boolean retainAll(Collection collection);

    int size();

    Spliterator spliterator();

    Object[] toArray();

    Object[] toArray(Object[] objArr);

    /* renamed from: j$.util.Set$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Spliterator $default$spliterator(java.util.Set _this) {
            return k0.m(_this, 1);
        }
    }
}
