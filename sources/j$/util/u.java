package j$.util;

import j$.util.function.Consumer;
import j$.util.function.l;
import java.util.Comparator;

public interface u {

    public interface a extends w {
        boolean b(Consumer consumer);

        void c(l lVar);

        void forEachRemaining(Consumer consumer);

        boolean g(l lVar);

        a trySplit();
    }

    boolean b(Consumer consumer);

    int characteristics();

    long estimateSize();

    void forEachRemaining(Consumer consumer);

    Comparator getComparator();

    long getExactSizeIfKnown();

    boolean hasCharacteristics(int i);

    u trySplit();
}
