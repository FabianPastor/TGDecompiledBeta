package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.function.u;
import j$.util.m;
import j$.util.p;
import j$.util.t;

/* renamed from: j$.util.stream.s1  reason: case insensitive filesystem */
public interface CLASSNAMEs1 extends CLASSNAMEl1<Double, CLASSNAMEs1> {
    p C(j$.util.function.p pVar);

    Object D(J j, G g, BiConsumer biConsumer);

    double G(double d, j$.util.function.p pVar);

    CLASSNAMEs1 H(u uVar);

    Stream I(r rVar);

    boolean J(s sVar);

    boolean O(s sVar);

    boolean W(s sVar);

    p average();

    Stream boxed();

    long count();

    CLASSNAMEs1 d(q qVar);

    CLASSNAMEs1 distinct();

    p findAny();

    p findFirst();

    t.a iterator();

    void k0(q qVar);

    void l(q qVar);

    CLASSNAMEs1 limit(long j);

    p max();

    p min();

    C1 o(j$.J j);

    CLASSNAMEs1 parallel();

    CLASSNAMEs1 sequential();

    CLASSNAMEs1 skip(long j);

    CLASSNAMEs1 sorted();

    Spliterator.a spliterator();

    double sum();

    m summaryStatistics();

    CLASSNAMEs1 t(s sVar);

    double[] toArray();

    CLASSNAMEs1 u(r rVar);

    H1 v(j$.util.function.t tVar);
}
