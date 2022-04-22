package j$.util.stream;

import j$.util.CLASSNAMEg;
import j$.util.CLASSNAMEj;
import j$.util.CLASSNAMEn;
import j$.util.function.BiConsumer;
import j$.util.function.d;
import j$.util.function.f;
import j$.util.function.g;
import j$.util.function.h;
import j$.util.function.u;
import j$.util.function.y;
import j$.wrappers.E;
import j$.wrappers.G;
import j$.wrappers.K;

public interface U extends CLASSNAMEg {
    CLASSNAMEj G(d dVar);

    Object H(y yVar, u uVar, BiConsumer biConsumer);

    double K(double d, d dVar);

    Stream M(g gVar);

    M0 R(G g);

    boolean Y(E e);

    CLASSNAMEj average();

    U b(f fVar);

    Stream boxed();

    long count();

    U distinct();

    CLASSNAMEj findAny();

    CLASSNAMEj findFirst();

    boolean h0(E e);

    boolean i0(E e);

    CLASSNAMEn iterator();

    void j(f fVar);

    void l0(f fVar);

    U limit(long j);

    CLASSNAMEj max();

    CLASSNAMEj min();

    U parallel();

    U r(E e);

    U sequential();

    U skip(long j);

    U sorted();

    j$.util.u spliterator();

    double sum();

    CLASSNAMEg summaryStatistics();

    double[] toArray();

    U w(g gVar);

    CLASSNAMEf1 x(h hVar);

    U y(K k);
}
