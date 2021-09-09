package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEp;
import j$.util.function.BiConsumer;
import j$.util.function.j;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.n;
import j$.util.function.v;
import j$.util.function.z;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;

public interface M0 extends CLASSNAMEg {
    U A(X x);

    boolean C(V v);

    boolean F(V v);

    void I(l lVar);

    Stream J(m mVar);

    int N(int i, j jVar);

    M0 P(m mVar);

    void U(l lVar);

    CLASSNAMEk a0(j jVar);

    U asDoubleStream();

    CLASSNAMEf1 asLongStream();

    CLASSNAMEj average();

    Stream boxed();

    M0 c0(l lVar);

    long count();

    M0 distinct();

    CLASSNAMEf1 f(n nVar);

    CLASSNAMEk findAny();

    CLASSNAMEk findFirst();

    M0 h(V v);

    CLASSNAMEp iterator();

    Object k0(z zVar, v vVar, BiConsumer biConsumer);

    M0 limit(long j);

    CLASSNAMEk max();

    CLASSNAMEk min();

    M0 parallel();

    M0 q(CLASSNAMEb0 b0Var);

    M0 sequential();

    M0 skip(long j);

    M0 sorted();

    j$.util.v spliterator();

    int sum();

    CLASSNAMEh summaryStatistics();

    int[] toArray();

    boolean v(V v);
}
