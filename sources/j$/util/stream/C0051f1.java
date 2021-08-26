package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEl;
import j$.util.function.BiConsumer;
import j$.util.function.n;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.s;
import j$.util.function.v;
import j$.util.function.y;
import j$.util.r;
import j$.util.w;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;

/* renamed from: j$.util.stream.f1  reason: case insensitive filesystem */
public interface CLASSNAMEf1 extends CLASSNAMEg {
    long D(long j, n nVar);

    boolean L(CLASSNAMEj0 j0Var);

    U O(CLASSNAMEl0 l0Var);

    Stream Q(q qVar);

    boolean S(CLASSNAMEj0 j0Var);

    void Z(p pVar);

    U asDoubleStream();

    CLASSNAMEj average();

    Stream boxed();

    long count();

    void d(p pVar);

    CLASSNAMEf1 distinct();

    M0 e0(CLASSNAMEn0 n0Var);

    Object f0(y yVar, v vVar, BiConsumer biConsumer);

    CLASSNAMEl findAny();

    CLASSNAMEl findFirst();

    CLASSNAMEl g(n nVar);

    r iterator();

    boolean k(CLASSNAMEj0 j0Var);

    CLASSNAMEf1 limit(long j);

    CLASSNAMEl max();

    CLASSNAMEl min();

    CLASSNAMEf1 p(p pVar);

    CLASSNAMEf1 parallel();

    CLASSNAMEf1 s(q qVar);

    CLASSNAMEf1 sequential();

    CLASSNAMEf1 skip(long j);

    CLASSNAMEf1 sorted();

    w spliterator();

    long sum();

    CLASSNAMEi summaryStatistics();

    long[] toArray();

    CLASSNAMEf1 u(CLASSNAMEj0 j0Var);

    CLASSNAMEf1 z(s sVar);
}
