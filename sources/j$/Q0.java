package j$;

import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.k;
import j$.util.n;
import j$.util.p;
import j$.util.q;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.CLASSNAMEs1;
import j$.util.stream.C1;
import j$.util.stream.H1;
import j$.util.stream.Stream;
import java.util.stream.IntStream;

public final /* synthetic */ class Q0 implements C1 {
    final /* synthetic */ IntStream a;

    private /* synthetic */ Q0(IntStream intStream) {
        this.a = intStream;
    }

    public static /* synthetic */ C1 m0(IntStream intStream) {
        if (intStream == null) {
            return null;
        }
        return intStream instanceof R0 ? ((R0) intStream).a : new Q0(intStream);
    }

    public /* synthetic */ void E(w wVar) {
        this.a.forEachOrdered(V.a(wVar));
    }

    public /* synthetic */ Stream F(x xVar) {
        return U0.m0(this.a.mapToObj(X.a(xVar)));
    }

    public /* synthetic */ int K(int i, v vVar) {
        return this.a.reduce(i, T.a(vVar));
    }

    public /* synthetic */ boolean L(y yVar) {
        return this.a.allMatch(Z.a(yVar));
    }

    public /* synthetic */ C1 M(x xVar) {
        return m0(this.a.flatMap(X.a(xVar)));
    }

    public /* synthetic */ void Q(w wVar) {
        this.a.forEach(V.a(wVar));
    }

    public /* synthetic */ boolean R(y yVar) {
        return this.a.noneMatch(Z.a(yVar));
    }

    public /* synthetic */ C1 X(y yVar) {
        return m0(this.a.filter(Z.a(yVar)));
    }

    public /* synthetic */ q Z(v vVar) {
        return k.l(this.a.reduce(T.a(vVar)));
    }

    public /* synthetic */ C1 a0(w wVar) {
        return m0(this.a.peek(V.a(wVar)));
    }

    public /* synthetic */ CLASSNAMEs1 asDoubleStream() {
        return O0.m0(this.a.asDoubleStream());
    }

    public /* synthetic */ H1 asLongStream() {
        return S0.m0(this.a.asLongStream());
    }

    public /* synthetic */ p average() {
        return k.k(this.a.average());
    }

    public /* synthetic */ boolean b(y yVar) {
        return this.a.anyMatch(Z.a(yVar));
    }

    public /* synthetic */ Stream boxed() {
        return U0.m0(this.a.boxed());
    }

    public /* synthetic */ void close() {
        this.a.close();
    }

    public /* synthetic */ long count() {
        return this.a.count();
    }

    public /* synthetic */ C1 distinct() {
        return m0(this.a.distinct());
    }

    public /* synthetic */ q findAny() {
        return k.l(this.a.findAny());
    }

    public /* synthetic */ q findFirst() {
        return k.l(this.a.findFirst());
    }

    public /* synthetic */ H1 h(z zVar) {
        return S0.m0(this.a.mapToLong(CLASSNAMEd0.a(zVar)));
    }

    public /* synthetic */ CLASSNAMEs1 i0(CLASSNAMEa0 a0Var) {
        return O0.m0(this.a.mapToDouble(a0Var == null ? null : a0Var.a));
    }

    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    public /* synthetic */ Object j0(J j, H h, BiConsumer biConsumer) {
        return this.a.collect(D0.a(j), x0.a(h), CLASSNAMEu.a(biConsumer));
    }

    public /* synthetic */ C1 limit(long j) {
        return m0(this.a.limit(j));
    }

    public /* synthetic */ q max() {
        return k.l(this.a.max());
    }

    public /* synthetic */ q min() {
        return k.l(this.a.min());
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return K0.m0(this.a.onClose(runnable));
    }

    public /* synthetic */ C1 skip(long j) {
        return m0(this.a.skip(j));
    }

    public /* synthetic */ C1 sorted() {
        return m0(this.a.sorted());
    }

    public /* synthetic */ int sum() {
        return this.a.sum();
    }

    public n summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.IntSummaryStatistics");
    }

    public /* synthetic */ int[] toArray() {
        return this.a.toArray();
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return K0.m0(this.a.unordered());
    }

    public /* synthetic */ C1 y(A a2) {
        return m0(this.a.map(CLASSNAMEf0.a(a2)));
    }
}
