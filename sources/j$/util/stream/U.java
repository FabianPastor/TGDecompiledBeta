package j$.util.stream;

import j$.util.CLASSNAMEg;
import j$.util.CLASSNAMEj;
import j$.util.InterfaceCLASSNAMEn;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public interface U extends InterfaceCLASSNAMEg {
    CLASSNAMEj G(j$.util.function.d dVar);

    Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer);

    double K(double d, j$.util.function.d dVar);

    Stream M(j$.util.function.g gVar);

    IntStream R(j$.wrappers.G g);

    boolean Y(j$.wrappers.E e);

    CLASSNAMEj average();

    U b(j$.util.function.f fVar);

    Stream boxed();

    long count();

    U distinct();

    CLASSNAMEj findAny();

    CLASSNAMEj findFirst();

    boolean h0(j$.wrappers.E e);

    boolean i0(j$.wrappers.E e);

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: iterator */
    InterfaceCLASSNAMEn mo307iterator();

    void j(j$.util.function.f fVar);

    void l0(j$.util.function.f fVar);

    U limit(long j);

    CLASSNAMEj max();

    CLASSNAMEj min();

    @Override // j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: parallel */
    U mo308parallel();

    U r(j$.wrappers.E e);

    @Override // j$.util.stream.InterfaceCLASSNAMEg, j$.util.stream.IntStream
    /* renamed from: sequential */
    U mo309sequential();

    U skip(long j);

    U sorted();

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    /* renamed from: spliterator */
    j$.util.t mo310spliterator();

    double sum();

    CLASSNAMEg summaryStatistics();

    double[] toArray();

    U w(j$.util.function.g gVar);

    InterfaceCLASSNAMEe1 x(j$.util.function.h hVar);

    U y(j$.wrappers.K k);
}
