package j$.util.stream;

import j$.CLASSNAMEa0;
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
import j$.util.n;
import j$.util.p;
import j$.util.q;
import j$.util.t;

public interface C1 extends CLASSNAMEl1<Integer, C1> {
    void E(w wVar);

    Stream F(x xVar);

    int K(int i, v vVar);

    boolean L(y yVar);

    C1 M(x xVar);

    void Q(w wVar);

    boolean R(y yVar);

    C1 X(y yVar);

    q Z(v vVar);

    C1 a0(w wVar);

    CLASSNAMEs1 asDoubleStream();

    H1 asLongStream();

    p average();

    boolean b(y yVar);

    Stream boxed();

    long count();

    C1 distinct();

    q findAny();

    q findFirst();

    H1 h(z zVar);

    CLASSNAMEs1 i0(CLASSNAMEa0 a0Var);

    t.b iterator();

    Object j0(J j, H h, BiConsumer biConsumer);

    C1 limit(long j);

    q max();

    q min();

    C1 parallel();

    C1 sequential();

    C1 skip(long j);

    C1 sorted();

    Spliterator.b spliterator();

    int sum();

    n summaryStatistics();

    int[] toArray();

    C1 y(A a);
}
