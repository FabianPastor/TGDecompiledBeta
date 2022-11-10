package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public interface Iterator<E> {

    /* renamed from: j$.util.Iterator$-CC  reason: invalid class name */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static void $default$forEachRemaining(java.util.Iterator it, Consumer consumer) {
            consumer.getClass();
            while (it.hasNext()) {
                consumer.accept(it.next());
            }
        }

        public static void a(java.util.Iterator it) {
            throw new UnsupportedOperationException("remove");
        }
    }

    void forEachRemaining(Consumer<? super E> consumer);

    boolean hasNext();

    /* renamed from: next */
    E moNUMnext();

    void remove();
}
