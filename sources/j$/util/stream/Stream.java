package j$.util.stream;

import j$.util.Optional;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.J;
import j$.util.function.Predicate;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.n;
import j$.util.function.x;
import java.util.Comparator;

public interface Stream<T> extends CLASSNAMEl1<T, Stream<T>> {
    CLASSNAMEs1 B(Function function);

    Stream P(Predicate predicate);

    Stream S(Consumer consumer);

    Object T(CLASSNAMEm1 m1Var);

    boolean U(Predicate predicate);

    H1 V(Function function);

    boolean a(Predicate predicate);

    boolean c0(Predicate predicate);

    long count();

    Stream distinct();

    C1 e(Function function);

    H1 e0(ToLongFunction toLongFunction);

    Optional findAny();

    Optional findFirst();

    void forEach(Consumer consumer);

    void g(Consumer consumer);

    CLASSNAMEs1 h0(ToDoubleFunction toDoubleFunction);

    Object k(J j, BiConsumer biConsumer, BiConsumer biConsumer2);

    Object l0(Object obj, n nVar);

    Stream limit(long j);

    C1 m(ToIntFunction toIntFunction);

    Optional max(Comparator comparator);

    Optional min(Comparator comparator);

    Stream n(Function function);

    Stream p(Function function);

    Optional s(n nVar);

    Stream skip(long j);

    Stream sorted();

    Stream sorted(Comparator comparator);

    Object[] toArray();

    Object[] toArray(x xVar);

    Object z(Object obj, BiFunction biFunction, n nVar);
}
