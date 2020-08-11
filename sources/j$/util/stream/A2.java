package j$.util.stream;

import j$.util.CLASSNAMEs;
import j$.util.H;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.G;
import j$.util.function.S;
import j$.util.function.V;
import j$.util.function.z;

public interface A2 extends CLASSNAMEl1 {
    void F(B b);

    Stream G(C c);

    int L(int i, z zVar);

    boolean M(D d);

    A2 N(C c);

    void S(B b);

    boolean T(D d);

    M1 V(E e);

    A2 a0(D d);

    M1 asDoubleStream();

    W2 asLongStream();

    j$.util.B average();

    boolean b(D d);

    Stream boxed();

    j$.util.C c0(z zVar);

    long count();

    A2 d0(B b);

    A2 distinct();

    j$.util.C findAny();

    j$.util.C findFirst();

    H iterator();

    W2 k(F f);

    Object l0(V v, S s, BiConsumer biConsumer);

    A2 limit(long j);

    j$.util.C max();

    j$.util.C min();

    A2 parallel();

    A2 sequential();

    A2 skip(long j);

    A2 sorted();

    j$.util.S spliterator();

    int sum();

    CLASSNAMEs summaryStatistics();

    int[] toArray();

    A2 z(G g);
}
