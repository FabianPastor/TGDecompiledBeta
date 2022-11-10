package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.function.BiConsumer;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;
/* renamed from: j$.util.stream.e1  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public interface InterfaceCLASSNAMEe1 extends InterfaceCLASSNAMEg {
    long D(long j, j$.util.function.o oVar);

    boolean L(CLASSNAMEj0 CLASSNAMEj0);

    U O(CLASSNAMEl0 CLASSNAMEl0);

    Stream Q(j$.util.function.r rVar);

    boolean S(CLASSNAMEj0 CLASSNAMEj0);

    void Z(j$.util.function.q qVar);

    U asDoubleStream();

    CLASSNAMEj average();

    Stream boxed();

    long count();

    void d(j$.util.function.q qVar);

    InterfaceCLASSNAMEe1 distinct();

    IntStream e0(CLASSNAMEn0 CLASSNAMEn0);

    Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer);

    CLASSNAMEl findAny();

    CLASSNAMEl findFirst();

    CLASSNAMEl g(j$.util.function.o oVar);

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    j$.util.r moNUMiterator();

    boolean k(CLASSNAMEj0 CLASSNAMEj0);

    InterfaceCLASSNAMEe1 limit(long j);

    CLASSNAMEl max();

    CLASSNAMEl min();

    InterfaceCLASSNAMEe1 p(j$.util.function.q qVar);

    @Override // j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    InterfaceCLASSNAMEe1 moNUMparallel();

    InterfaceCLASSNAMEe1 s(j$.util.function.r rVar);

    @Override // j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    InterfaceCLASSNAMEe1 moNUMsequential();

    InterfaceCLASSNAMEe1 skip(long j);

    InterfaceCLASSNAMEe1 sorted();

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    j$.util.v moNUMspliterator();

    long sum();

    CLASSNAMEi summaryStatistics();

    long[] toArray();

    InterfaceCLASSNAMEe1 u(CLASSNAMEj0 CLASSNAMEj0);

    InterfaceCLASSNAMEe1 z(j$.util.function.t tVar);
}
