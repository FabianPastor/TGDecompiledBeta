package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.CLASSNAMEp;
import j$.util.function.BiConsumer;
import j$.util.function.i;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.u;
import j$.util.function.y;
import j$.util.v;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;

public interface M0 extends CLASSNAMEg {
    U A(X x);

    boolean C(V v);

    boolean F(V v);

    void I(k kVar);

    Stream J(l lVar);

    int N(int i, i iVar);

    M0 P(l lVar);

    void U(k kVar);

    CLASSNAMEk a0(i iVar);

    U asDoubleStream();

    CLASSNAMEf1 asLongStream();

    CLASSNAMEj average();

    Stream boxed();

    M0 c0(k kVar);

    long count();

    M0 distinct();

    CLASSNAMEf1 f(m mVar);

    CLASSNAMEk findAny();

    CLASSNAMEk findFirst();

    M0 h(V v);

    CLASSNAMEp iterator();

    Object k0(y yVar, u uVar, BiConsumer biConsumer);

    M0 limit(long j);

    CLASSNAMEk max();

    CLASSNAMEk min();

    M0 parallel();

    M0 q(CLASSNAMEb0 b0Var);

    M0 sequential();

    M0 skip(long j);

    M0 sorted();

    v spliterator();

    int sum();

    CLASSNAMEh summaryStatistics();

    int[] toArray();

    boolean v(V v);
}
