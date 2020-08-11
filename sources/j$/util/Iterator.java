package j$.util;

import j$.util.function.Consumer;

public interface Iterator {
    void forEachRemaining(Consumer consumer);

    boolean hasNext();

    Object next();

    void remove();

    /* renamed from: j$.util.Iterator$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void a(java.util.Iterator _this) {
            throw new UnsupportedOperationException("remove");
        }

        public static void $default$forEachRemaining(java.util.Iterator _this, Consumer consumer) {
            consumer.getClass();
            while (_this.hasNext()) {
                consumer.accept(_this.next());
            }
        }
    }
}
