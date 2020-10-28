package j$.util.stream;

import j$.H;
import j$.J;
import j$.N;
import j$.util.C;
import j$.util.CLASSNAMEn;
import j$.util.CLASSNAMEt;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.E;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.x;

public interface L1 extends CLASSNAMEl1 {
    CLASSNAMEt C(p pVar);

    Object D(E e, B b, BiConsumer biConsumer);

    double H(double d, p pVar);

    L1 I(N n);

    Stream J(r rVar);

    L1 O(H h);

    boolean V(H h);

    CLASSNAMEt average();

    boolean b(H h);

    Stream boxed();

    L1 c(q qVar);

    long count();

    L1 distinct();

    CLASSNAMEt findAny();

    CLASSNAMEt findFirst();

    boolean h0(H h);

    x iterator();

    void k(q qVar);

    void k0(q qVar);

    L1 limit(long j);

    CLASSNAMEt max();

    CLASSNAMEt min();

    CLASSNAMEx2 o(J j);

    L1 parallel();

    L1 sequential();

    L1 skip(long j);

    L1 sorted();

    C spliterator();

    double sum();

    CLASSNAMEn summaryStatistics();

    double[] toArray();

    L1 u(r rVar);

    T2 v(s sVar);
}
