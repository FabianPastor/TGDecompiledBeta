package j$.util.stream;

import j$.X;
import j$.util.Spliterator;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.m;
import j$.util.o;
import j$.util.p;
import j$.util.s;

public interface C1 extends CLASSNAMEl1<Integer, C1> {
    void E(w wVar);

    Stream F(x xVar);

    int K(int i, v vVar);

    boolean L(y yVar);

    C1 M(x xVar);

    void Q(w wVar);

    boolean R(y yVar);

    C1 X(y yVar);

    p Z(v vVar);

    C1 a0(w wVar);

    CLASSNAMEs1 asDoubleStream();

    H1 asLongStream();

    o average();

    boolean b(y yVar);

    Stream boxed();

    long count();

    C1 distinct();

    p findAny();

    p findFirst();

    H1 h(z zVar);

    CLASSNAMEs1 i0(X x);

    s.b iterator();

    Object j0(J j, H h, BiConsumer biConsumer);

    C1 limit(long j);

    p max();

    p min();

    C1 parallel();

    C1 sequential();

    C1 skip(long j);

    C1 sorted();

    Spliterator.b spliterator();

    int sum();

    m summaryStatistics();

    int[] toArray();

    C1 y(A a);
}
