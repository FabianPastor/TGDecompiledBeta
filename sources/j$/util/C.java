package j$.util;

import j$.util.function.Consumer;
import j$.util.function.q;

public interface C extends F {
    boolean b(Consumer consumer);

    void e(q qVar);

    void forEachRemaining(Consumer consumer);

    boolean o(q qVar);

    C trySplit();
}
