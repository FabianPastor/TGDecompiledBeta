package j$.util;

import j$.util.function.UnaryOperator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

public interface List extends Collection, Collection {

    /* renamed from: j$.util.List$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$replaceAll(java.util.List list, UnaryOperator unaryOperator) {
            if (DesugarCollections.b.isInstance(list)) {
                DesugarCollections.e(list, unaryOperator);
                return;
            }
            unaryOperator.getClass();
            ListIterator listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                listIterator.set(unaryOperator.apply(listIterator.next()));
            }
        }

        public static void $default$sort(java.util.List list, Comparator comparator) {
            if (DesugarCollections.b.isInstance(list)) {
                DesugarCollections.f(list, comparator);
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

        public static Spliterator $default$spliterator(java.util.List list) {
            list.getClass();
            return new T((Collection) list, 16);
        }
    }

    void add(int i, Object obj);

    boolean add(Object obj);

    boolean addAll(int i, Collection collection);

    boolean addAll(Collection collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(Collection collection);

    boolean equals(Object obj);

    Object get(int i);

    int hashCode();

    int indexOf(Object obj);

    boolean isEmpty();

    Iterator iterator();

    int lastIndexOf(Object obj);

    ListIterator listIterator();

    ListIterator listIterator(int i);

    Object remove(int i);

    boolean remove(Object obj);

    boolean removeAll(Collection collection);

    void replaceAll(UnaryOperator unaryOperator);

    boolean retainAll(Collection collection);

    Object set(int i, Object obj);

    int size();

    void sort(Comparator comparator);

    Spliterator spliterator();

    java.util.List subList(int i, int i2);

    Object[] toArray();

    Object[] toArray(Object[] objArr);
}
