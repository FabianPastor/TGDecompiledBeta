package j$.util;

import j$.util.function.Consumer;
import j$.util.function.q;

public interface v extends w {
    boolean b(Consumer consumer);

    void d(q qVar);

    void forEachRemaining(Consumer consumer);

    boolean i(q qVar);

    v trySplit();
}
