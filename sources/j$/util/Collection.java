package j$.util;

import j$.lang.Iterable;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.stream.Stream;
import j$.util.stream.b7;
import java.util.Iterator;

public interface Collection extends Iterable, Iterable {
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

    Object[] toArray(C c);

    Object[] toArray(Object[] objArr);

    /* renamed from: j$.util.Collection$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Object[] b(java.util.Collection _this, C c) {
            return _this.toArray((Object[]) c.a(0));
        }

        public static boolean $default$removeIf(java.util.Collection _this, Predicate predicate) {
            if (DesugarCollections.a.isInstance(_this)) {
                return DesugarCollections.f(_this, predicate);
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

        public static void a(java.util.Collection _this, Consumer consumer) {
            consumer.getClass();
            Iterator it = _this.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
        }

        public static Spliterator $default$spliterator(java.util.Collection _this) {
            return k0.m(_this, 0);
        }

        public static Stream $default$stream(java.util.Collection _this) {
            return b7.d(CLASSNAMEk.c(_this), false);
        }

        public static Stream $default$parallelStream(java.util.Collection _this) {
            return b7.d(CLASSNAMEk.c(_this), true);
        }
    }
}
