package j$.util;

import j$.util.function.UnaryOperator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

public interface List extends Collection, Collection {
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

    /* renamed from: j$.util.List$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$replaceAll(java.util.List _this, UnaryOperator unaryOperator) {
            if (DesugarCollections.b.isInstance(_this)) {
                DesugarCollections.g(_this, unaryOperator);
                return;
            }
            unaryOperator.getClass();
            ListIterator<E> li = _this.listIterator();
            while (li.hasNext()) {
                li.set(unaryOperator.apply(li.next()));
            }
        }

        public static void $default$sort(java.util.List _this, Comparator c) {
            if (DesugarCollections.b.isInstance(_this)) {
                DesugarCollections.h(_this, c);
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
            return k0.m(_this, 16);
        }
    }
}
