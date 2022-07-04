package j$.lang;

import j$.util.Collection;
import j$.util.DesugarCollections;
import j$.util.DesugarLinkedHashSet;
import j$.util.List;
import j$.util.Set;
import j$.util.SortedSet;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.Consumer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public interface Iterable<T> {

    /* renamed from: j$.lang.Iterable$-EL  reason: invalid class name */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void forEach(Iterable iterable, Consumer consumer) {
            if (iterable instanceof Iterable) {
                ((Iterable) iterable).forEach(consumer);
            } else if (iterable instanceof Collection) {
                Collection.CC.$default$forEach((java.util.Collection) iterable, consumer);
            } else {
                CC.$default$forEach(iterable, consumer);
            }
        }

        public static /* synthetic */ Spliterator spliterator(Iterable iterable) {
            return iterable instanceof Iterable ? ((Iterable) iterable).spliterator() : iterable instanceof LinkedHashSet ? DesugarLinkedHashSet.spliterator((LinkedHashSet) iterable) : iterable instanceof SortedSet ? SortedSet.CC.$default$spliterator((java.util.SortedSet) iterable) : iterable instanceof Set ? Set.CC.$default$spliterator((java.util.Set) iterable) : iterable instanceof List ? List.CC.$default$spliterator((java.util.List) iterable) : iterable instanceof java.util.Collection ? Collection.CC.$default$spliterator((java.util.Collection) iterable) : CC.$default$spliterator(iterable);
        }
    }

    void forEach(Consumer<? super T> consumer);

    Iterator<T> iterator();

    Spliterator<T> spliterator();

    /* renamed from: j$.lang.Iterable$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$forEach(Iterable _this, Consumer consumer) {
            if (DesugarCollections.SYNCHRONIZED_COLLECTION.isInstance(_this)) {
                DesugarCollections.forEach(_this, consumer);
                return;
            }
            consumer.getClass();
            Iterator it = _this.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
        }

        public static Spliterator $default$spliterator(Iterable _this) {
            return Spliterators.spliteratorUnknownSize(_this.iterator(), 0);
        }
    }
}
