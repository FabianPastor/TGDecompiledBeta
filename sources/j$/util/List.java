package j$.util;

import j$.util.function.UnaryOperator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

public interface List<E> extends Collection<E> {

    /* renamed from: j$.util.List$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void replaceAll(java.util.List list, UnaryOperator unaryOperator) {
            if (list instanceof List) {
                ((List) list).replaceAll(unaryOperator);
            } else {
                CC.$default$replaceAll(list, unaryOperator);
            }
        }

        public static /* synthetic */ void sort(java.util.List list, Comparator comparator) {
            if (list instanceof List) {
                ((List) list).sort(comparator);
            } else {
                CC.$default$sort(list, comparator);
            }
        }

        public static /* synthetic */ Spliterator spliterator(java.util.List list) {
            return list instanceof List ? ((List) list).spliterator() : CC.$default$spliterator(list);
        }
    }

    void add(int i, E e);

    boolean add(E e);

    boolean addAll(int i, Collection<? extends E> collection);

    boolean addAll(Collection<? extends E> collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(Collection<?> collection);

    boolean equals(Object obj);

    E get(int i);

    int hashCode();

    int indexOf(Object obj);

    boolean isEmpty();

    Iterator<E> iterator();

    int lastIndexOf(Object obj);

    ListIterator<E> listIterator();

    ListIterator<E> listIterator(int i);

    E remove(int i);

    boolean remove(Object obj);

    boolean removeAll(Collection<?> collection);

    void replaceAll(UnaryOperator<E> unaryOperator);

    boolean retainAll(Collection<?> collection);

    E set(int i, E e);

    int size();

    void sort(Comparator<? super E> comparator);

    Spliterator<E> spliterator();

    java.util.List<E> subList(int i, int i2);

    Object[] toArray();

    <T> T[] toArray(T[] tArr);

    /* renamed from: j$.util.List$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$replaceAll(java.util.List _this, UnaryOperator unaryOperator) {
            if (DesugarCollections.SYNCHRONIZED_LIST.isInstance(_this)) {
                DesugarCollections.replaceAll(_this, unaryOperator);
                return;
            }
            unaryOperator.getClass();
            ListIterator<E> li = _this.listIterator();
            while (li.hasNext()) {
                li.set(unaryOperator.apply(li.next()));
            }
        }

        public static void $default$sort(java.util.List _this, Comparator c) {
            if (DesugarCollections.SYNCHRONIZED_LIST.isInstance(_this)) {
                DesugarCollections.sort(_this, c);
                return;
            }
            Object[] a = _this.toArray();
            Arrays.sort(a, c);
            ListIterator<E> i = _this.listIterator();
            for (Object e : a) {
                i.next();
                i.set(e);
            }
        }

        public static Spliterator $default$spliterator(java.util.List _this) {
            return Spliterators.spliterator(_this, 16);
        }
    }
}
