package j$;

import j$.time.a;
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
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEm1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.$r8$wrapper$java$util$stream$Stream$-V-WRP  reason: invalid class name */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$Stream$VWRP implements Stream {
    final /* synthetic */ java.util.stream.Stream a;

    private /* synthetic */ C$r8$wrapper$java$util$stream$Stream$VWRP(java.util.stream.Stream stream) {
        this.a = stream;
    }

    public static /* synthetic */ Stream convert(java.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof R0 ? ((R0) stream).a : new C$r8$wrapper$java$util$stream$Stream$VWRP(stream);
    }

    public /* synthetic */ CLASSNAMEs1 B(Function function) {
        return L0.m0(this.a.flatMapToDouble(N.a(function)));
    }

    public /* synthetic */ Stream P(Predicate predicate) {
        return convert(this.a.filter(y0.a(predicate)));
    }

    public /* synthetic */ Stream S(Consumer consumer) {
        return convert(this.a.peek(CLASSNAMEx.a(consumer)));
    }

    public /* synthetic */ Object T(CLASSNAMEm1 m1Var) {
        return this.a.collect(K0.a(m1Var));
    }

    public /* synthetic */ boolean U(Predicate predicate) {
        return this.a.allMatch(y0.a(predicate));
    }

    public /* synthetic */ H1 V(Function function) {
        return P0.m0(this.a.flatMapToLong(N.a(function)));
    }

    public /* synthetic */ boolean a(Predicate predicate) {
        return this.a.anyMatch(y0.a(predicate));
    }

    public /* synthetic */ boolean c0(Predicate predicate) {
        return this.a.noneMatch(y0.a(predicate));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ Stream distinct() {
        return convert(this.a.distinct());
    }

    public /* synthetic */ C1 e(Function function) {
        return N0.m0(this.a.flatMapToInt(N.a(function)));
    }

    public /* synthetic */ H1 e0(ToLongFunction toLongFunction) {
        return P0.m0(this.a.mapToLong(G0.a(toLongFunction)));
    }

    public /* synthetic */ Optional findAny() {
        return a.j(this.a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return a.j(this.a.findFirst());
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ void g(Consumer consumer) {
        this.a.forEachOrdered(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ CLASSNAMEs1 h0(ToDoubleFunction toDoubleFunction) {
        return L0.m0(this.a.mapToDouble(C0.a(toDoubleFunction)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ Object k(J j, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.collect(A0.a(j), r.a(biConsumer), r.a(biConsumer2));
    }

    public /* synthetic */ Object l0(Object obj, n nVar) {
        return this.a.reduce(obj, CLASSNAMEv.a(nVar));
    }

    public /* synthetic */ Stream limit(long j) {
        return convert(this.a.limit(j));
    }

    public /* synthetic */ C1 m(ToIntFunction toIntFunction) {
        return N0.m0(this.a.mapToInt(E0.a(toIntFunction)));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return a.j(this.a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return a.j(this.a.min(comparator));
    }

    public /* synthetic */ Stream n(Function function) {
        return convert(this.a.map(N.a(function)));
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return H0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ Stream p(Function function) {
        return convert(this.a.flatMap(N.a(function)));
    }

    public /* synthetic */ CLASSNAMEl1 parallel() {
        return H0.m0(this.a.parallel());
    }

    public /* synthetic */ Optional s(n nVar) {
        return a.j(this.a.reduce(CLASSNAMEv.a(nVar)));
    }

    public /* synthetic */ CLASSNAMEl1 sequential() {
        return H0.m0(this.a.sequential());
    }

    public /* synthetic */ Stream skip(long j) {
        return convert(this.a.skip(j));
    }

    public /* synthetic */ Stream sorted() {
        return convert(this.a.sorted());
    }

    public /* synthetic */ Stream sorted(Comparator comparator) {
        return convert(this.a.sorted(comparator));
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ Object[] toArray(x xVar) {
        return this.a.toArray(U.a(xVar));
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return H0.m0(this.a.unordered());
    }

    public /* synthetic */ Object z(Object obj, BiFunction biFunction, n nVar) {
        return this.a.reduce(obj, CLASSNAMEt.a(biFunction), CLASSNAMEv.a(nVar));
    }
}
