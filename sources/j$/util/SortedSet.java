package j$.util;

import j$.util.Spliterators;
import java.util.Comparator;

public interface SortedSet<E> extends Set<E> {

    /* renamed from: j$.util.SortedSet$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Spliterator spliterator(java.util.SortedSet sortedSet) {
            return sortedSet instanceof SortedSet ? ((SortedSet) sortedSet).spliterator() : CC.$default$spliterator(sortedSet);
        }
    }

    Comparator<? super E> comparator();

    E first();

    java.util.SortedSet<E> headSet(E e);

    E last();

    Spliterator<E> spliterator();

    java.util.SortedSet<E> subSet(E e, E e2);

    java.util.SortedSet<E> tailSet(E e);

    /* renamed from: j$.util.SortedSet$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Spliterator $default$spliterator(java.util.SortedSet _this) {
            return new Spliterators.IteratorSpliterator<E>(_this, _this, 21) {
                final /* synthetic */ java.util.SortedSet this$0;

                {
                    this.this$0 = this$0;
                }

                public Comparator<? super E> getComparator() {
                    return this.this$0.comparator();
                }
            };
        }
    }
}
