package j$.util.stream;

import j$.util.CLASSNAMEz;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.C;
import j$.util.function.CLASSNAMEo;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.function.V;
import java.util.Comparator;

public interface Stream extends CLASSNAMEl1 {
    Object A(Object obj, BiFunction biFunction, CLASSNAMEo oVar);

    M1 C(Function function);

    Stream R(Predicate predicate);

    Stream U(Consumer consumer);

    Object W(CLASSNAMEn1 n1Var);

    boolean X(Predicate predicate);

    W2 Y(Function function);

    boolean a(Predicate predicate);

    long count();

    Stream distinct();

    boolean f0(Predicate predicate);

    CLASSNAMEz findAny();

    CLASSNAMEz findFirst();

    void forEach(Consumer consumer);

    A2 h(Function function);

    W2 h0(ToLongFunction toLongFunction);

    void j(Consumer consumer);

    M1 k0(ToDoubleFunction toDoubleFunction);

    Stream limit(long j);

    Object m(V v, BiConsumer biConsumer, BiConsumer biConsumer2);

    CLASSNAMEz max(Comparator comparator);

    CLASSNAMEz min(Comparator comparator);

    A2 o(ToIntFunction toIntFunction);

    Object o0(Object obj, CLASSNAMEo oVar);

    Stream p(Function function);

    Stream r(Function function);

    Stream skip(long j);

    Stream sorted();

    Stream sorted(Comparator comparator);

    Object[] toArray();

    Object[] toArray(C c);

    CLASSNAMEz u(CLASSNAMEo oVar);
}
