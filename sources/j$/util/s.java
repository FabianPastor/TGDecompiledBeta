package j$.util;

import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import java.util.Iterator;

public interface s<T, T_CONS> extends Iterator<T>, Iterator {

    public interface a extends s<Double, q> {
        void e(q qVar);

        void forEachRemaining(Consumer consumer);

        Double next();

        double nextDouble();
    }

    public interface b extends s<Integer, w> {
        void c(w wVar);

        void forEachRemaining(Consumer consumer);

        Integer next();

        int nextInt();
    }

    public interface c extends s<Long, C> {
        void d(C c);

        void forEachRemaining(Consumer consumer);

        Long next();

        long nextLong();
    }

    void forEachRemaining(Object obj);
}
