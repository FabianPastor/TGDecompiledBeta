package j$.util.stream;

import j$.util.Optional;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.C;
import j$.util.function.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.m;
import j$.util.function.y;
import j$.util.function.z;
import j$.wrappers.J0;
import java.util.Comparator;

public interface Stream<T> extends CLASSNAMEg {
    Object B(Object obj, BiFunction biFunction, CLASSNAMEb bVar);

    U E(Function function);

    Stream T(y yVar);

    Stream V(Consumer consumer);

    boolean W(y yVar);

    CLASSNAMEf1 X(Function function);

    boolean a(y yVar);

    Object b0(J0 j0);

    M0 c(Function function);

    /* synthetic */ void close();

    long count();

    boolean d0(y yVar);

    Stream distinct();

    void e(Consumer consumer);

    Optional findAny();

    Optional findFirst();

    void forEach(Consumer<? super T> consumer);

    CLASSNAMEf1 g0(C c);

    Object i(z zVar, BiConsumer biConsumer, BiConsumer biConsumer2);

    U j0(A a);

    Object[] l(m mVar);

    Stream limit(long j);

    M0 m(B b);

    Object m0(Object obj, CLASSNAMEb bVar);

    Optional max(Comparator comparator);

    Optional min(Comparator comparator);

    Stream n(Function function);

    Stream o(Function function);

    Stream skip(long j);

    Stream sorted();

    Stream sorted(Comparator comparator);

    Optional t(CLASSNAMEb bVar);

    Object[] toArray();
}
