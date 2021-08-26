package j$.util;

import j$.util.function.Consumer;
import j$.util.function.p;

public interface w extends x {
    boolean b(Consumer consumer);

    void d(p pVar);

    void forEachRemaining(Consumer consumer);

    boolean i(p pVar);

    w trySplit();
}
