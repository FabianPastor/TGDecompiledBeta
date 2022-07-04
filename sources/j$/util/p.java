package j$.util;

import j$.util.function.Consumer;
import j$.util.function.l;
import java.util.Iterator;

public interface p extends Iterator {

    public interface a extends p {
        void c(l lVar);

        void forEachRemaining(Consumer consumer);

        Integer next();

        int nextInt();
    }

    void forEachRemaining(Object obj);
}
