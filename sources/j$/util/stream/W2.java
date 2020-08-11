package j$.util.stream;

import j$.util.B;
import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.U;
import j$.util.function.BiConsumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.K;
import j$.util.function.L;
import j$.util.function.M;
import j$.util.function.N;
import j$.util.function.P;
import j$.util.function.T;
import j$.util.function.V;

public interface W2 extends CLASSNAMEl1 {
    long B(long j, H h);

    A2 O(N n);

    Stream P(K k);

    M1 asDoubleStream();

    B average();

    void b0(J j);

    Stream boxed();

    long count();

    W2 distinct();

    boolean e0(L l);

    boolean f(L l);

    D findAny();

    D findFirst();

    Object g0(V v, T t, BiConsumer biConsumer);

    void i(J j);

    boolean i0(L l);

    j$.util.J iterator();

    W2 j0(L l);

    D l(H h);

    W2 limit(long j);

    D max();

    D min();

    W2 parallel();

    M1 q(M m);

    W2 s(J j);

    W2 sequential();

    W2 skip(long j);

    W2 sorted();

    U spliterator();

    long sum();

    CLASSNAMEw summaryStatistics();

    W2 t(K k);

    long[] toArray();

    W2 y(P p);
}
