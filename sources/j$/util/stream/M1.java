package j$.util.stream;

import j$.util.B;
import j$.util.CLASSNAMEq;
import j$.util.F;
import j$.util.P;
import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEt;
import j$.util.function.CLASSNAMEu;
import j$.util.function.CLASSNAMEv;
import j$.util.function.Q;
import j$.util.function.V;
import j$.util.function.r;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;

public interface M1 extends CLASSNAMEl1 {
    B D(r rVar);

    Object E(V v, Q q, BiConsumer biConsumer);

    double H(double d, r rVar);

    M1 I(y yVar);

    Stream J(CLASSNAMEu uVar);

    boolean K(CLASSNAMEv vVar);

    boolean Q(CLASSNAMEv vVar);

    boolean Z(CLASSNAMEv vVar);

    B average();

    Stream boxed();

    long count();

    M1 distinct();

    B findAny();

    B findFirst();

    M1 g(CLASSNAMEt tVar);

    F iterator();

    M1 limit(long j);

    void m0(CLASSNAMEt tVar);

    B max();

    B min();

    void n(CLASSNAMEt tVar);

    A2 n0(w wVar);

    M1 parallel();

    M1 sequential();

    M1 skip(long j);

    M1 sorted();

    P spliterator();

    double sum();

    CLASSNAMEq summaryStatistics();

    double[] toArray();

    M1 v(CLASSNAMEv vVar);

    M1 w(CLASSNAMEu uVar);

    W2 x(x xVar);
}
