package j$;

import j$.util.Optional;
import j$.util.Spliterator;
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
import j$.util.k;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEm1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.Comparator;
import java.util.Iterator;

public final /* synthetic */ class U0 implements Stream {
    final /* synthetic */ java.util.stream.Stream a;

    private /* synthetic */ U0(java.util.stream.Stream stream) {
        this.a = stream;
    }

    public static /* synthetic */ Stream m0(java.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof V0 ? ((V0) stream).a : new U0(stream);
    }

    public /* synthetic */ CLASSNAMEs1 B(Function function) {
        return O0.m0(this.a.flatMapToDouble(Q.a(function)));
    }

    public /* synthetic */ Stream P(Predicate predicate) {
        return m0(this.a.filter(B0.a(predicate)));
    }

    public /* synthetic */ Stream S(Consumer consumer) {
        return m0(this.a.peek(A.a(consumer)));
    }

    public /* synthetic */ Object T(CLASSNAMEm1 m1Var) {
        return this.a.collect(N0.a(m1Var));
    }

    public /* synthetic */ boolean U(Predicate predicate) {
        return this.a.allMatch(B0.a(predicate));
    }

    public /* synthetic */ H1 V(Function function) {
        return S0.m0(this.a.flatMapToLong(Q.a(function)));
    }

    public /* synthetic */ boolean a(Predicate predicate) {
        return this.a.anyMatch(B0.a(predicate));
    }

    public /* synthetic */ boolean c0(Predicate predicate) {
        return this.a.noneMatch(B0.a(predicate));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ Stream distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ C1 e(Function function) {
        return Q0.m0(this.a.flatMapToInt(Q.a(function)));
    }

    public /* synthetic */ H1 e0(ToLongFunction toLongFunction) {
        return S0.m0(this.a.mapToLong(J0.a(toLongFunction)));
    }

    public /* synthetic */ Optional findAny() {
        return k.j(this.a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return k.j(this.a.findFirst());
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(A.a(consumer));
    }

    public /* synthetic */ void g(Consumer consumer) {
        this.a.forEachOrdered(A.a(consumer));
    }

    public /* synthetic */ CLASSNAMEs1 h0(ToDoubleFunction toDoubleFunction) {
        return O0.m0(this.a.mapToDouble(F0.a(toDoubleFunction)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ Object k(J j, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.collect(D0.a(j), CLASSNAMEu.a(biConsumer), CLASSNAMEu.a(biConsumer2));
    }

    public /* synthetic */ Object l0(Object obj, n nVar) {
        return this.a.reduce(obj, CLASSNAMEy.a(nVar));
    }

    public /* synthetic */ Stream limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ C1 m(ToIntFunction toIntFunction) {
        return Q0.m0(this.a.mapToInt(H0.a(toIntFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return k.j(this.a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return k.j(this.a.min(comparator));
    }

    public /* synthetic */ Stream n(Function function) {
        return m0(this.a.map(Q.a(function)));
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ Stream p(Function function) {
        return m0(this.a.flatMap(Q.a(function)));
    }

    public /* synthetic */ CLASSNAMEl1 parallel() {
        return K0.m0(this.a.parallel());
    }

    public /* synthetic */ Optional s(n nVar) {
        return k.j(this.a.reduce(CLASSNAMEy.a(nVar)));
    }

    public /* synthetic */ CLASSNAMEl1 sequential() {
        return K0.m0(this.a.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return m0(this.a.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEj.a(this.a.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ Object[] toArray(x xVar) {
        return this.a.toArray(X.a(xVar));
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.a.unordered());
    }

    public /* synthetic */ Object z(Object obj, BiFunction biFunction, n nVar) {
        return this.a.reduce(obj, CLASSNAMEw.a(biFunction), CLASSNAMEy.a(nVar));
    }
}
