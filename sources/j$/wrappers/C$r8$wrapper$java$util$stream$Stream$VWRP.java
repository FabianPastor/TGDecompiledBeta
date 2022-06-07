package j$.wrappers;

import j$.util.CLASSNAMEa;
import j$.util.Optional;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToIntFunction;
import j$.util.function.b;
import j$.util.function.m;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.stream.CLASSNAMEe1;
import j$.util.stream.CLASSNAMEg;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import j$.util.stream.U;
import j$.util.u;
import java.util.Comparator;
import java.util.Iterator;

/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$Stream$-V-WRP  reason: invalid class name */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$Stream$VWRP implements Stream {
    final /* synthetic */ java.util.stream.Stream a;

    private /* synthetic */ C$r8$wrapper$java$util$stream$Stream$VWRP(java.util.stream.Stream stream) {
        this.a = stream;
    }

    public static /* synthetic */ Stream convert(java.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof P0 ? ((P0) stream).a : new C$r8$wrapper$java$util$stream$Stream$VWRP(stream);
    }

    public /* synthetic */ Object B(Object obj, BiFunction biFunction, b bVar) {
        return this.a.reduce(obj, CLASSNAMEt.a(biFunction), CLASSNAMEv.a(bVar));
    }

    public /* synthetic */ U E(Function function) {
        return L0.n0(this.a.flatMapToDouble(N.a(function)));
    }

    public /* synthetic */ Stream T(Predicate predicate) {
        return convert(this.a.filter(y0.a(predicate)));
    }

    public /* synthetic */ Stream V(Consumer consumer) {
        return convert(this.a.peek(CLASSNAMEx.a(consumer)));
    }

    public /* synthetic */ boolean W(Predicate predicate) {
        return this.a.allMatch(y0.a(predicate));
    }

    public /* synthetic */ CLASSNAMEe1 X(Function function) {
        return N0.n0(this.a.flatMapToLong(N.a(function)));
    }

    public /* synthetic */ boolean a(Predicate predicate) {
        return this.a.anyMatch(y0.a(predicate));
    }

    public /* synthetic */ Object b0(J0 j0) {
        return this.a.collect(j0 == null ? null : j0.a);
    }

    public /* synthetic */ IntStream c(Function function) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.flatMapToInt(N.a(function)));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ boolean d0(Predicate predicate) {
        return this.a.noneMatch(y0.a(predicate));
    }

    public /* synthetic */ Stream distinct() {
        return convert(this.a.distinct());
    }

    public /* synthetic */ void e(Consumer consumer) {
        this.a.forEachOrdered(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ Optional findAny() {
        return CLASSNAMEa.p(this.a.findAny());
    }

    public /* synthetic */ Optional findFirst() {
        return CLASSNAMEa.p(this.a.findFirst());
    }

    public /* synthetic */ void forEach(Consumer consumer) {
        this.a.forEach(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ CLASSNAMEe1 g0(A a2) {
        return N0.n0(this.a.mapToLong(G0.a(a2)));
    }

    public /* synthetic */ Object i(y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.collect(A0.a(yVar), r.a(biConsumer), r.a(biConsumer2));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ U j0(z zVar) {
        return L0.n0(this.a.mapToDouble(C0.a(zVar)));
    }

    public /* synthetic */ Object[] l(m mVar) {
        return this.a.toArray(U.a(mVar));
    }

    public /* synthetic */ Stream limit(long j) {
        return convert(this.a.limit(j));
    }

    public /* synthetic */ IntStream m(ToIntFunction toIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.mapToInt(E0.a(toIntFunction)));
    }

    public /* synthetic */ Object m0(Object obj, b bVar) {
        return this.a.reduce(obj, CLASSNAMEv.a(bVar));
    }

    public /* synthetic */ Optional max(Comparator comparator) {
        return CLASSNAMEa.p(this.a.max(comparator));
    }

    public /* synthetic */ Optional min(Comparator comparator) {
        return CLASSNAMEa.p(this.a.min(comparator));
    }

    public /* synthetic */ Stream n(Function function) {
        return convert(this.a.map(N.a(function)));
    }

    public /* synthetic */ Stream o(Function function) {
        return convert(this.a.flatMap(N.a(function)));
    }

    public /* synthetic */ CLASSNAMEg onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEg parallel() {
        return H0.n0(this.a.parallel());
    }

    public /* synthetic */ CLASSNAMEg sequential() {
        return H0.n0(this.a.sequential());
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

    public /* synthetic */ u spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    public /* synthetic */ Optional t(b bVar) {
        return CLASSNAMEa.p(this.a.reduce(CLASSNAMEv.a(bVar)));
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }
}
