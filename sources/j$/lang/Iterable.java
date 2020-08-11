package j$.lang;

import j$.util.DesugarCollections;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.k0;
import java.util.Iterator;

public interface Iterable {
    void forEach(Consumer consumer);

    Iterator iterator();

    Spliterator spliterator();

    /* renamed from: j$.lang.Iterable$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$forEach(Iterable _this, Consumer consumer) {
            if (DesugarCollections.a.isInstance(_this)) {
                DesugarCollections.c(_this, consumer);
                return;
            }
            consumer.getClass();
            Iterator it = _this.iterator();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
        }

        public static Spliterator $default$spliterator(Iterable _this) {
            return k0.o(_this.iterator(), 0);
        }
    }
}
