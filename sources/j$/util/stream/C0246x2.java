package j$.util.stream;

import j$.CLASSNAMEa0;
import j$.CLASSNAMEe0;
import j$.Y;
import j$.util.CLASSNAMEp;
import j$.util.CLASSNAMEt;
import j$.util.CLASSNAMEu;
import j$.util.D;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.E;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.y;

/* renamed from: j$.util.stream.x2  reason: case insensitive filesystem */
public interface CLASSNAMEx2 extends CLASSNAMEl1 {
    void E(u uVar);

    Stream F(v vVar);

    int K(int i, t tVar);

    boolean L(Y y);

    CLASSNAMEx2 M(v vVar);

    void Q(u uVar);

    CLASSNAMEx2 W(CLASSNAMEe0 e0Var);

    CLASSNAMEu Y(t tVar);

    CLASSNAMEx2 Z(Y y);

    CLASSNAMEx2 a0(u uVar);

    L1 asDoubleStream();

    T2 asLongStream();

    CLASSNAMEt average();

    Stream boxed();

    long count();

    CLASSNAMEx2 distinct();

    boolean e0(Y y);

    CLASSNAMEu findAny();

    CLASSNAMEu findFirst();

    T2 g(w wVar);

    L1 g0(CLASSNAMEa0 a0Var);

    boolean i0(Y y);

    y iterator();

    Object j0(E e, C c, BiConsumer biConsumer);

    CLASSNAMEx2 limit(long j);

    CLASSNAMEu max();

    CLASSNAMEu min();

    CLASSNAMEx2 parallel();

    CLASSNAMEx2 sequential();

    CLASSNAMEx2 skip(long j);

    CLASSNAMEx2 sorted();

    D spliterator();

    int sum();

    CLASSNAMEp summaryStatistics();

    int[] toArray();
}
