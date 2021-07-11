package j$;

import j$.time.a;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.n;
import j$.util.o;
import j$.util.q;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.stream.LongStream;

public final /* synthetic */ class P0 implements H1 {
    final /* synthetic */ LongStream a;

    private /* synthetic */ P0(LongStream longStream) {
        this.a = longStream;
    }

    public static /* synthetic */ H1 m0(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof Q0 ? ((Q0) longStream).a : new P0(longStream);
    }

    public /* synthetic */ long A(long j, B b) {
        return this.a.reduce(j, CLASSNAMEe0.a(b));
    }

    public /* synthetic */ Stream N(D d) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(CLASSNAMEi0.a(d)));
    }

    public /* synthetic */ void Y(C c) {
        this.a.forEachOrdered(CLASSNAMEg0.a(c));
    }

    public /* synthetic */ CLASSNAMEs1 asDoubleStream() {
        return L0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ o average() {
        return a.k(this.a.average());
    }

    public /* synthetic */ boolean b0(E e) {
        return this.a.anyMatch(CLASSNAMEk0.a(e));
    }

    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    public /* synthetic */ boolean c(E e) {
        return this.a.noneMatch(CLASSNAMEk0.a(e));
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ Object d0(J j, I i, BiConsumer biConsumer) {
        return this.a.collect(A0.a(j), w0.a(i), r.a(biConsumer));
    }

    public /* synthetic */ H1 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ void f(C c) {
        this.a.forEach(CLASSNAMEg0.a(c));
    }

    public /* synthetic */ boolean f0(E e) {
        return this.a.allMatch(CLASSNAMEk0.a(e));
    }

    public /* synthetic */ q findAny() {
        return a.m(this.a.findAny());
    }

    public /* synthetic */ q findFirst() {
        return a.m(this.a.findFirst());
    }

    public /* synthetic */ H1 g0(E e) {
        return m0(this.a.filter(CLASSNAMEk0.a(e)));
    }

    public /* synthetic */ q i(B b) {
        return a.m(this.a.reduce(CLASSNAMEe0.a(b)));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ CLASSNAMEs1 j(CLASSNAMEl0 l0Var) {
        return L0.m0(this.a.mapToDouble(l0Var == null ? null : l0Var.a));
    }

    public /* synthetic */ H1 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ q max() {
        return a.m(this.a.max());
    }

    public /* synthetic */ q min() {
        return a.m(this.a.min());
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return H0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ H1 q(C c) {
        return m0(this.a.peek(CLASSNAMEg0.a(c)));
    }

    public /* synthetic */ H1 r(D d) {
        return m0(this.a.flatMap(CLASSNAMEi0.a(d)));
    }

    public /* synthetic */ H1 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ H1 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    public n summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }

    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return H0.m0(this.a.unordered());
    }

    public /* synthetic */ C1 w(CLASSNAMEn0 n0Var) {
        return N0.m0(this.a.mapToInt(n0Var == null ? null : n0Var.a));
    }

    public /* synthetic */ H1 x(F f) {
        return m0(this.a.map(CLASSNAMEq0.a(f)));
    }
}
