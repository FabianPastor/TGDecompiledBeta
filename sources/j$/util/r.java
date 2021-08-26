package j$.util;

import j$.util.function.Consumer;
import j$.util.function.p;

public interface r extends s {
    void d(p pVar);

    void forEachRemaining(Consumer consumer);

    Long next();

    long nextLong();
}
