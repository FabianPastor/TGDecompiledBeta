package j$.util;

import j$.util.function.Consumer;
import j$.util.function.f;

public interface t extends w {
    boolean b(Consumer consumer);

    void e(f fVar);

    void forEachRemaining(Consumer consumer);

    boolean k(f fVar);

    t trySplit();
}
