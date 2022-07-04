package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.function.j;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.n;
import j$.util.function.v;
import j$.util.function.y;
import j$.util.p;
import j$.util.u;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;

public interface IntStream extends CLASSNAMEg {
    U A(X x);

    boolean C(V v);

    boolean F(V v);

    void I(l lVar);

    Stream J(m mVar);

    int N(int i, j jVar);

    IntStream P(m mVar);

    void U(l lVar);

    CLASSNAMEk a0(j jVar);

    U asDoubleStream();

    CLASSNAMEe1 asLongStream();

    CLASSNAMEj average();

    Stream boxed();

    IntStream c0(l lVar);

    long count();

    IntStream distinct();

    CLASSNAMEe1 f(n nVar);

    CLASSNAMEk findAny();

    CLASSNAMEk findFirst();

    IntStream h(V v);

    p.a iterator();

    Object k0(y yVar, v vVar, BiConsumer biConsumer);

    IntStream limit(long j);

    CLASSNAMEk max();

    CLASSNAMEk min();

    IntStream parallel();

    IntStream q(CLASSNAMEb0 b0Var);

    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    u.a spliterator();

    int sum();

    CLASSNAMEh summaryStatistics();

    int[] toArray();

    boolean v(V v);
}
