package j$.util;

import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import java.util.Comparator;

public interface Spliterator<T> {

    public interface a extends d<Double, q, a> {
        boolean b(Consumer consumer);

        void e(q qVar);

        void forEachRemaining(Consumer consumer);

        boolean o(q qVar);

        a trySplit();
    }

    public interface b extends d<Integer, w, b> {
        boolean b(Consumer consumer);

        void c(w wVar);

        void forEachRemaining(Consumer consumer);

        boolean h(w wVar);

        b trySplit();
    }

    public interface c extends d<Long, C, c> {
        boolean b(Consumer consumer);

        void d(C c);

        void forEachRemaining(Consumer consumer);

        boolean j(C c);

        c trySplit();
    }

    public interface d<T, T_CONS, T_SPLITR extends d<T, T_CONS, T_SPLITR>> extends Spliterator<T> {
        void forEachRemaining(Object obj);

        boolean tryAdvance(Object obj);

        d trySplit();
    }

    boolean b(Consumer consumer);

    int characteristics();

    long estimateSize();

    void forEachRemaining(Consumer consumer);

    Comparator getComparator();

    long getExactSizeIfKnown();

    boolean hasCharacteristics(int i);

    Spliterator trySplit();
}
