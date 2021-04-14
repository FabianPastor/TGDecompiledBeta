package j$.util.stream;

import a.CLASSNAMEo0;
import a.CLASSNAMEq0;
import j$.util.Spliterator;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.o;
import j$.util.p;
import j$.util.r;
import j$.util.t;

public interface H1 extends CLASSNAMEl1<Long, H1> {
    long A(long j, B b);

    Stream N(D d);

    void Y(C c);

    CLASSNAMEs1 asDoubleStream();

    p average();

    boolean b0(E e);

    Stream boxed();

    boolean c(E e);

    long count();

    Object d0(J j, I i, BiConsumer biConsumer);

    H1 distinct();

    void f(C c);

    boolean f0(E e);

    r findAny();

    r findFirst();

    H1 g0(E e);

    r i(B b);

    t.c iterator();

    CLASSNAMEs1 j(CLASSNAMEo0 o0Var);

    H1 limit(long j);

    r max();

    r min();

    H1 parallel();

    H1 q(C c);

    H1 r(D d);

    H1 sequential();

    H1 skip(long j);

    H1 sorted();

    Spliterator.c spliterator();

    long sum();

    o summaryStatistics();

    long[] toArray();

    C1 w(CLASSNAMEq0 q0Var);

    H1 x(F f);
}
