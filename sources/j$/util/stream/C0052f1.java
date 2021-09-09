package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.function.BiConsumer;
import j$.util.function.o;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.t;
import j$.util.function.w;
import j$.util.function.z;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;

/* renamed from: j$.util.stream.f1  reason: case insensitive filesystem */
public interface CLASSNAMEf1 extends CLASSNAMEg {
    long D(long j, o oVar);

    boolean L(CLASSNAMEj0 j0Var);

    U O(CLASSNAMEl0 l0Var);

    Stream Q(r rVar);

    boolean S(CLASSNAMEj0 j0Var);

    void Z(q qVar);

    U asDoubleStream();

    CLASSNAMEj average();

    Stream boxed();

    long count();

    void d(q qVar);

    CLASSNAMEf1 distinct();

    M0 e0(CLASSNAMEn0 n0Var);

    Object f0(z zVar, w wVar, BiConsumer biConsumer);

    CLASSNAMEl findAny();

    CLASSNAMEl findFirst();

    CLASSNAMEl g(o oVar);

    j$.util.r iterator();

    boolean k(CLASSNAMEj0 j0Var);

    CLASSNAMEf1 limit(long j);

    CLASSNAMEl max();

    CLASSNAMEl min();

    CLASSNAMEf1 p(q qVar);

    CLASSNAMEf1 parallel();

    CLASSNAMEf1 s(r rVar);

    CLASSNAMEf1 sequential();

    CLASSNAMEf1 skip(long j);

    CLASSNAMEf1 sorted();

    j$.util.w spliterator();

    long sum();

    CLASSNAMEi summaryStatistics();

    long[] toArray();

    CLASSNAMEf1 u(CLASSNAMEj0 j0Var);

    CLASSNAMEf1 z(t tVar);
}
