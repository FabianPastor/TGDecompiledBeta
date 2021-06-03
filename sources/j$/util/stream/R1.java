package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.stream.A2;

interface R1<T> {

    public interface a<T> extends A2<T> {

        /* renamed from: j$.util.stream.R1$a$a  reason: collision with other inner class name */
        public interface CLASSNAMEa extends a<Double>, A2.e {
            b a();
        }

        public interface b extends a<Integer>, A2.f {
            c a();
        }

        public interface c extends a<Long>, A2.g {
            d a();
        }

        R1 a();
    }

    public interface b extends e<Double, q, double[], Spliterator.a, b> {
    }

    public interface c extends e<Integer, w, int[], Spliterator.b, c> {
    }

    public interface d extends e<Long, C, long[], Spliterator.c, d> {
    }

    public interface e<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.d<T, T_CONS, T_SPLITR>, T_NODE extends e<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends R1<T> {
        e b(int i);

        Object c(int i);

        void d(Object obj, int i);

        Object e();

        void g(Object obj);

        Spliterator.d spliterator();
    }

    R1 b(int i);

    long count();

    void forEach(Consumer consumer);

    void i(Object[] objArr, int i);

    int n();

    Object[] p(x xVar);

    R1 q(long j, long j2, x xVar);

    Spliterator spliterator();
}
