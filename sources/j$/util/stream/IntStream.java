package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEk;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.CLASSNAMEb0;
/* loaded from: classes2.dex */
public interface IntStream extends InterfaceCLASSNAMEg {
    U A(j$.wrappers.X x);

    boolean C(j$.wrappers.V v);

    boolean F(j$.wrappers.V v);

    void I(j$.util.function.l lVar);

    Stream J(j$.util.function.m mVar);

    int N(int i, j$.util.function.j jVar);

    IntStream P(j$.util.function.m mVar);

    void U(j$.util.function.l lVar);

    CLASSNAMEk a0(j$.util.function.j jVar);

    U asDoubleStream();

    InterfaceCLASSNAMEe1 asLongStream();

    CLASSNAMEj average();

    Stream boxed();

    IntStream c0(j$.util.function.l lVar);

    long count();

    IntStream distinct();

    InterfaceCLASSNAMEe1 f(j$.util.function.n nVar);

    CLASSNAMEk findAny();

    CLASSNAMEk findFirst();

    IntStream h(j$.wrappers.V v);

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    p.a moNUMiterator();

    Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer);

    IntStream limit(long j);

    CLASSNAMEk max();

    CLASSNAMEk min();

    @Override // 
    /* renamed from: parallel */
    IntStream moNUMparallel();

    IntStream q(CLASSNAMEb0 CLASSNAMEb0);

    @Override // 
    /* renamed from: sequential */
    IntStream moNUMsequential();

    IntStream skip(long j);

    IntStream sorted();

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    u.a moNUMspliterator();

    int sum();

    CLASSNAMEh summaryStatistics();

    int[] toArray();

    boolean v(j$.wrappers.V v);
}
