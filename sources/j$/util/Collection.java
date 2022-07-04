package j$.util;

import j$.lang.Iterable;
import j$.util.List;
import j$.util.Set;
import j$.util.SortedSet;
import j$.util.function.Consumer;
import j$.util.function.IntFunction;
import j$.util.function.Predicate;
import j$.util.stream.Stream;
import j$.util.stream.StreamSupport;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public interface Collection<E> extends Iterable<E> {

    /* renamed from: j$.util.Collection$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void forEach(java.util.Collection collection, Consumer consumer) {
            if (collection instanceof Collection) {
                ((Collection) collection).forEach(consumer);
            } else {
                CC.$default$forEach(collection, consumer);
            }
        }

        public static /* synthetic */ Stream parallelStream(java.util.Collection collection) {
            return collection instanceof Collection ? ((Collection) collection).parallelStream() : CC.$default$parallelStream(collection);
        }

        public static /* synthetic */ boolean removeIf(java.util.Collection collection, Predicate predicate) {
            return collection instanceof Collection ? ((Collection) collection).removeIf(predicate) : CC.$default$removeIf(collection, predicate);
        }

        public static /* synthetic */ Spliterator spliterator(java.util.Collection collection) {
            return collection instanceof Collection ? ((Collection) collection).spliterator() : collection instanceof LinkedHashSet ? DesugarLinkedHashSet.spliterator((LinkedHashSet) collection) : collection instanceof SortedSet ? SortedSet.CC.$default$spliterator((java.util.SortedSet) collection) : collection instanceof Set ? Set.CC.$default$spliterator((java.util.Set) collection) : collection instanceof List ? List.CC.$default$spliterator((java.util.List) collection) : CC.$default$spliterator(collection);
        }

        public static /* synthetic */ Stream stream(java.util.Collection collection) {
            return collection instanceof Collection ? ((Collection) collection).stream() : CC.$default$stream(collection);
        }

        public static /* synthetic */ Object[] toArray(java.util.Collection collection, IntFunction intFunction) {
            return collection instanceof Collection ? ((Collection) collection).toArray((IntFunction<T[]>) intFunction) : CC.$default$toArray(collection, intFunction);
        }
    }

    boolean add(E e);

    boolean addAll(java.util.Collection<? extends E> collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(java.util.Collection<?> collection);

    boolean equals(Object obj);

    void forEach(Consumer<? super E> consumer);

    int hashCode();

    boolean isEmpty();

    Iterator<E> iterator();

    Stream<E> parallelStream();

    boolean remove(Object obj);

    boolean removeAll(java.util.Collection<?> collection);

    boolean removeIf(Predicate<? super E> predicate);

    boolean retainAll(java.util.Collection<?> collection);

    int size();

    Spliterator<E> spliterator();

    Stream<E> stream();

    Object[] toArray();

    <T> T[] toArray(IntFunction<T[]> intFunction);

    <T> T[] toArray(T[] tArr);

    /* renamed from: j$.util.Collection$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static <T> Object[] $default$toArray(java.util.Collection _this, IntFunction intFunction) {
            return _this.toArray((Object[]) intFunction.apply(0));
        }

        public static boolean $default$removeIf(java.util.Collection _this, Predicate predicate) {
            if (DesugarCollections.SYNCHRONIZED_COLLECTION.isInstance(_this)) {
                return DesugarCollections.removeIf(_this, predicate);
            }
            predicate.getClass();
            boolean removed = false;
            Iterator<E> each = _this.iterator();
            while (each.hasNext()) {
                if (predicate.test(each.next())) {
                    each.remove();
                    removed = true;
                }
            }
            return removed;
        }

        public static void $default$forEach(java.util.Collection _this, Consumer consumer) {
            consumer.getClass();
            Iterator it = _this.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
        }

        public static Spliterator $default$spliterator(java.util.Collection _this) {
            return Spliterators.spliterator(_this, 0);
        }

        public static Stream $default$stream(java.util.Collection _this) {
            return StreamSupport.stream(EL.spliterator(_this), false);
        }

        public static Stream $default$parallelStream(java.util.Collection _this) {
            return StreamSupport.stream(EL.spliterator(_this), true);
        }
    }
}
