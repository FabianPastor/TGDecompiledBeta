package j$.util;

import j$.lang.Iterable;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.v;
import j$.util.stream.CLASSNAMEc3;
import j$.util.stream.Stream;
import java.util.Iterator;

public interface Collection extends Iterable, Iterable {

    /* renamed from: j$.util.Collection$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Stream $default$parallelStream(java.util.Collection collection) {
            return CLASSNAMEc3.v(CLASSNAMEk.C(collection), true);
        }

        public static boolean $default$removeIf(java.util.Collection collection, Predicate predicate) {
            if (DesugarCollections.a.isInstance(collection)) {
                return DesugarCollections.d(collection, predicate);
            }
            predicate.getClass();
            boolean z = false;
            Iterator it = collection.iterator();
            while (it.hasNext()) {
                if (predicate.test(it.next())) {
                    it.remove();
                    z = true;
                }
            }
            return z;
        }

        public static Spliterator $default$spliterator(java.util.Collection collection) {
            collection.getClass();
            return new T(collection, 0);
        }

        public static Stream $default$stream(java.util.Collection collection) {
            return CLASSNAMEc3.v(CLASSNAMEk.C(collection), false);
        }
    }

    boolean add(Object obj);

    boolean addAll(java.util.Collection collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(java.util.Collection collection);

    boolean equals(Object obj);

    void forEach(Consumer consumer);

    int hashCode();

    boolean isEmpty();

    Iterator iterator();

    Stream parallelStream();

    boolean remove(Object obj);

    boolean removeAll(java.util.Collection collection);

    boolean removeIf(Predicate predicate);

    boolean retainAll(java.util.Collection collection);

    int size();

    Spliterator spliterator();

    Stream stream();

    Object[] toArray();

    Object[] toArray(v vVar);

    Object[] toArray(Object[] objArr);
}
