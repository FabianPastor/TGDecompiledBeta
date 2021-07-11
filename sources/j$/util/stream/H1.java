package j$.util.stream;

import j$.CLASSNAMEl0;
import j$.CLASSNAMEn0;
import j$.util.Spliterator;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.n;
import j$.util.o;
import j$.util.q;
import j$.util.s;

public interface H1 extends CLASSNAMEl1<Long, H1> {
    long A(long j, B b);

    Stream N(D d);

    void Y(C c);

    CLASSNAMEs1 asDoubleStream();

    o average();

    boolean b0(E e);

    Stream boxed();

    boolean c(E e);

    long count();

    Object d0(J j, I i, BiConsumer biConsumer);

    H1 distinct();

    void f(C c);

    boolean f0(E e);

    q findAny();

    q findFirst();

    H1 g0(E e);

    q i(B b);

    s.c iterator();

    CLASSNAMEs1 j(CLASSNAMEl0 l0Var);

    H1 limit(long j);

    q max();

    q min();

    H1 parallel();

    H1 q(C c);

    H1 r(D d);

    H1 sequential();

    H1 skip(long j);

    H1 sorted();

    Spliterator.c spliterator();

    long sum();

    n summaryStatistics();

    long[] toArray();

    C1 w(CLASSNAMEn0 n0Var);

    H1 x(F f);
}
