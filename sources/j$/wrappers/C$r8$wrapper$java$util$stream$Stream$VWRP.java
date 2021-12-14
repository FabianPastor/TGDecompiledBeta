package j$.wrappers;

import j$.util.CLASSNAMEa;
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
import j$.util.stream.CLASSNAMEf1;
import j$.util.stream.CLASSNAMEg;
import j$.util.stream.M0;
import j$.util.stream.Stream;
import j$.util.stream.U;
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
        return stream instanceof R0 ? ((R0) stream).a : new C$r8$wrapper$java$util$stream$Stream$VWRP(stream);
    }

    public /* synthetic */ Object B(Object obj, BiFunction biFunction, CLASSNAMEb bVar) {
        return this.a.reduce(obj, CLASSNAMEt.a(biFunction), CLASSNAMEv.a(bVar));
    }

    public /* synthetic */ U E(Function function) {
        return L0.n0(this.a.flatMapToDouble(N.a(function)));
    }

    public /* synthetic */ Stream T(y yVar) {
        return convert(this.a.filter(y0.a(yVar)));
    }

    public /* synthetic */ Stream V(Consumer consumer) {
        return convert(this.a.peek(CLASSNAMEx.a(consumer)));
    }

    public /* synthetic */ boolean W(y yVar) {
        return this.a.allMatch(y0.a(yVar));
    }

    public /* synthetic */ CLASSNAMEf1 X(Function function) {
        return P0.n0(this.a.flatMapToLong(N.a(function)));
    }

    public /* synthetic */ boolean a(y yVar) {
        return this.a.anyMatch(y0.a(yVar));
    }

    public /* synthetic */ Object b0(J0 j0) {
        return this.a.collect(j0 == null ? null : j0.a);
    }

    public /* synthetic */ M0 c(Function function) {
        return N0.n0(this.a.flatMapToInt(N.a(function)));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ boolean d0(y yVar) {
        return this.a.noneMatch(y0.a(yVar));
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

    public /* synthetic */ CLASSNAMEf1 g0(C c) {
        return P0.n0(this.a.mapToLong(G0.a(c)));
    }

    public /* synthetic */ Object i(z zVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.a.collect(A0.a(zVar), r.a(biConsumer), r.a(biConsumer2));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.a.iterator();
    }

    public /* synthetic */ U j0(A a2) {
        return L0.n0(this.a.mapToDouble(C0.a(a2)));
    }

    public /* synthetic */ Object[] l(m mVar) {
        return this.a.toArray(U.a(mVar));
    }

    public /* synthetic */ Stream limit(long j) {
        return convert(this.a.limit(j));
    }

    public /* synthetic */ M0 m(B b) {
        return N0.n0(this.a.mapToInt(E0.a(b)));
    }

    public /* synthetic */ Object m0(Object obj, CLASSNAMEb bVar) {
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

    public /* synthetic */ j$.util.y spliterator() {
        return CLASSNAMEg.a(this.a.spliterator());
    }

    public /* synthetic */ Optional t(CLASSNAMEb bVar) {
        return CLASSNAMEa.p(this.a.reduce(CLASSNAMEv.a(bVar)));
    }

    public /* synthetic */ Object[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEg unordered() {
        return H0.n0(this.a.unordered());
    }
}
