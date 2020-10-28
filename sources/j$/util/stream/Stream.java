package j$.util.stream;

import j$.util.Optional;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.E;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.n;
import j$.util.function.v;
import java.util.Comparator;

public interface Stream extends CLASSNAMEl1 {
    L1 B(Function function);

    Stream P(Predicate predicate);

    Stream R(Consumer consumer);

    Object S(CLASSNAMEn1 n1Var);

    boolean T(Predicate predicate);

    T2 U(Function function);

    boolean a(Predicate predicate);

    boolean b0(Predicate predicate);

    long count();

    CLASSNAMEx2 d(Function function);

    T2 d0(ToLongFunction toLongFunction);

    Stream distinct();

    void f(Consumer consumer);

    L1 f0(ToDoubleFunction toDoubleFunction);

    Optional findAny();

    Optional findFirst();

    void forEach(Consumer consumer);

    Object j(E e, BiConsumer biConsumer, BiConsumer biConsumer2);

    Object l0(Object obj, n nVar);

    Stream limit(long j);

    CLASSNAMEx2 m(ToIntFunction toIntFunction);

    Optional max(Comparator comparator);

    Optional min(Comparator comparator);

    Stream n(Function function);

    Stream p(Function function);

    Stream skip(long j);

    Stream sorted();

    Stream sorted(Comparator comparator);

    Optional t(n nVar);

    Object[] toArray();

    Object[] toArray(v vVar);

    Object z(Object obj, BiFunction biFunction, n nVar);
}
