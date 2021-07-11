package j$.util;

import j$.util.u;
import java.util.Collection;
import java.util.Iterator;

public interface Set<E> extends Collection<E> {

    /* renamed from: j$.util.Set$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Spliterator $default$spliterator(java.util.Set set) {
            set.getClass();
            return new u.i((Collection) set, 1);
        }
    }

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
}
