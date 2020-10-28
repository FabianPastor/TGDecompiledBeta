package j$.util.stream;

import j$.CLASSNAMEm0;
import j$.CLASSNAMEo0;
import j$.CLASSNAMEq0;
import j$.util.CLASSNAMEt;
import j$.util.CLASSNAMEv;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.r;

public interface T2 extends CLASSNAMEl1 {
    long A(long j, x xVar);

    T2 G(CLASSNAMEm0 m0Var);

    Stream N(z zVar);

    void X(y yVar);

    L1 asDoubleStream();

    CLASSNAMEt average();

    Stream boxed();

    Object c0(E e, D d, BiConsumer biConsumer);

    long count();

    T2 distinct();

    void e(y yVar);

    CLASSNAMEv findAny();

    CLASSNAMEv findFirst();

    CLASSNAMEv h(x xVar);

    L1 i(CLASSNAMEo0 o0Var);

    j$.util.z iterator();

    boolean l(CLASSNAMEm0 m0Var);

    T2 limit(long j);

    CLASSNAMEv max();

    CLASSNAMEv min();

    T2 parallel();

    T2 q(y yVar);

    boolean r(CLASSNAMEm0 m0Var);

    T2 s(z zVar);

    T2 sequential();

    T2 skip(long j);

    T2 sorted();

    j$.util.E spliterator();

    long sum();

    r summaryStatistics();

    long[] toArray();

    CLASSNAMEx2 w(CLASSNAMEq0 q0Var);

    T2 x(A a);

    boolean y(CLASSNAMEm0 m0Var);
}
