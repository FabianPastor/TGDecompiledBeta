package j$.util;

import j$.util.function.Consumer;
import j$.util.function.q;

public interface r extends s {
    void d(q qVar);

    void forEachRemaining(Consumer consumer);

    Long next();

    long nextLong();
}
