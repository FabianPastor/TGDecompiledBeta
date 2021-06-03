package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.l;
import j$.util.o;
import j$.util.s;

/* renamed from: j$.util.stream.s1  reason: case insensitive filesystem */
public interface CLASSNAMEs1 extends CLASSNAMEl1<Double, CLASSNAMEs1> {
    o C(p pVar);

    Object D(J j, G g, BiConsumer biConsumer);

    double G(double d, p pVar);

    CLASSNAMEs1 H(u uVar);

    Stream I(r rVar);

    boolean J(s sVar);

    boolean O(s sVar);

    boolean W(s sVar);

    o average();

    Stream boxed();

    long count();

    CLASSNAMEs1 d(q qVar);

    CLASSNAMEs1 distinct();

    o findAny();

    o findFirst();

    s.a iterator();

    void k0(q qVar);

    void l(q qVar);

    CLASSNAMEs1 limit(long j);

    o max();

    o min();

    C1 o(j$.G g);

    CLASSNAMEs1 parallel();

    CLASSNAMEs1 sequential();

    CLASSNAMEs1 skip(long j);

    CLASSNAMEs1 sorted();

    Spliterator.a spliterator();

    double sum();

    l summaryStatistics();

    CLASSNAMEs1 t(j$.util.function.s sVar);

    double[] toArray();

    CLASSNAMEs1 u(r rVar);

    H1 v(t tVar);
}
