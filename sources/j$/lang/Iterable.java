package j$.lang;

import j$.util.DesugarCollections;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.Consumer;
import java.util.Iterator;

public interface Iterable {

    /* renamed from: j$.lang.Iterable$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$forEach(Iterable iterable, Consumer consumer) {
            if (DesugarCollections.a.isInstance(iterable)) {
                DesugarCollections.c(iterable, consumer);
                return;
            }
            consumer.getClass();
            for (Object accept : iterable) {
                consumer.accept(accept);
            }
        }

        public static Spliterator $default$spliterator(Iterable iterable) {
            return V.n(iterable.iterator(), 0);
        }
    }

    void forEach(Consumer consumer);

    Iterator iterator();

    Spliterator spliterator();
}
