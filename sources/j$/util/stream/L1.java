package j$.util.stream;

import j$.util.B;
import j$.util.CLASSNAMEq;
import j$.util.F;
import j$.util.P;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.CLASSNAMEt;
import j$.util.function.CLASSNAMEu;
import j$.util.function.CLASSNAMEv;
import j$.util.function.Q;
import j$.util.function.V;
import j$.util.function.r;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.k0;

abstract class L1 extends CLASSNAMEh1 implements M1 {
    public /* bridge */ /* synthetic */ M1 parallel() {
        super.parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ M1 sequential() {
        super.sequential();
        return this;
    }

    L1(Spliterator spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    L1(CLASSNAMEh1 upstream, int opFlags) {
        super(upstream, opFlags);
    }

    private static CLASSNAMEt P0(G5 sink) {
        if (sink instanceof CLASSNAMEt) {
            return (CLASSNAMEt) sink;
        }
        if (!h7.a) {
            sink.getClass();
            return new B(sink);
        }
        h7.b(CLASSNAMEh1.class, "using DoubleStream.adapt(Sink<Double> s)");
        throw null;
    }

    /* access modifiers changed from: private */
    public static P O0(Spliterator spliterator) {
        if (spliterator instanceof P) {
            return (P) spliterator;
        }
        if (h7.a) {
            h7.b(CLASSNAMEh1.class, "using DoubleStream.adapt(Spliterator<Double> s)");
            throw null;
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEv6 A0() {
        return CLASSNAMEv6.DOUBLE_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt3 y0(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree, C c) {
        return CLASSNAMEp4.g(helper, spliterator, flattenTree);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator M0(CLASSNAMEq4 ph, V v, boolean isParallel) {
        return new K6(ph, v, isParallel);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: a1 */
    public final P F0(V v) {
        return new E6(v);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0012, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r4, j$.util.stream.G5 r5) {
        /*
            r3 = this;
            j$.util.P r0 = O0(r4)
            j$.util.function.t r1 = P0(r5)
        L_0x0008:
            boolean r2 = r5.u()
            if (r2 != 0) goto L_0x0014
            boolean r2 = r0.j(r1)
            if (r2 != 0) goto L_0x0008
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.L1.z0(j$.util.Spliterator, j$.util.stream.G5):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEk3 s0(long exactSizeIfKnown, C c) {
        return CLASSNAMEp4.l(exactSizeIfKnown);
    }

    public final F iterator() {
        return k0.f(spliterator());
    }

    public final P spliterator() {
        return O0(super.spliterator());
    }

    public final Stream boxed() {
        return J(CLASSNAMEa.a);
    }

    public final M1 I(y mapper) {
        mapper.getClass();
        return new CLASSNAMEu1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final Stream J(CLASSNAMEu uVar) {
        uVar.getClass();
        return new CLASSNAMEw1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, uVar);
    }

    public final A2 n0(w mapper) {
        mapper.getClass();
        return new CLASSNAMEy1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final W2 x(x mapper) {
        mapper.getClass();
        return new A1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final M1 w(CLASSNAMEu uVar) {
        return new C1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, uVar);
    }

    /* renamed from: b1 */
    public M1 unordered() {
        if (!B0()) {
            return this;
        }
        return new D1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.w);
    }

    public final M1 v(CLASSNAMEv predicate) {
        predicate.getClass();
        return new F1(this, this, CLASSNAMEv6.DOUBLE_VALUE, CLASSNAMEu6.y, predicate);
    }

    public final M1 g(CLASSNAMEt action) {
        action.getClass();
        return new H1(this, this, CLASSNAMEv6.DOUBLE_VALUE, 0, action);
    }

    public final M1 limit(long maxSize) {
        if (maxSize >= 0) {
            return Q5.j(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final M1 skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return Q5.j(this, n, -1);
        }
    }

    public final M1 sorted() {
        return CLASSNAMEh6.a(this);
    }

    public final M1 distinct() {
        return ((CLASSNAMEu5) ((CLASSNAMEu5) boxed()).distinct()).k0(CLASSNAMEw.a);
    }

    public void n(CLASSNAMEt consumer) {
        w0(CLASSNAMEc2.a(consumer, false));
    }

    public void m0(CLASSNAMEt consumer) {
        w0(CLASSNAMEc2.a(consumer, true));
    }

    static /* synthetic */ double[] W0() {
        return new double[3];
    }

    public final double sum() {
        return CLASSNAMEo1.a((double[]) E(CLASSNAMEx.a, CLASSNAMEv.a, r.a));
    }

    static /* synthetic */ void X0(double[] ll, double d) {
        CLASSNAMEo1.b(ll, d);
        ll[2] = ll[2] + d;
    }

    static /* synthetic */ void Y0(double[] ll, double[] rr) {
        CLASSNAMEo1.b(ll, rr[0]);
        CLASSNAMEo1.b(ll, rr[1]);
        ll[2] = ll[2] + rr[2];
    }

    public final B min() {
        return D(J0.a);
    }

    public final B max() {
        return D(CLASSNAMEd1.a);
    }

    static /* synthetic */ double[] Q0() {
        return new double[4];
    }

    public final B average() {
        double[] avg = (double[]) E(CLASSNAMEs.a, CLASSNAMEy.a, CLASSNAMEp.a);
        if (avg[2] > 0.0d) {
            return B.d(CLASSNAMEo1.a(avg) / avg[2]);
        }
        return B.a();
    }

    static /* synthetic */ void R0(double[] ll, double d) {
        ll[2] = ll[2] + 1.0d;
        CLASSNAMEo1.b(ll, d);
        ll[3] = ll[3] + d;
    }

    static /* synthetic */ void S0(double[] ll, double[] rr) {
        CLASSNAMEo1.b(ll, rr[0]);
        CLASSNAMEo1.b(ll, rr[1]);
        ll[2] = ll[2] + rr[2];
        ll[3] = ll[3] + rr[3];
    }

    static /* synthetic */ long U0() {
        return 1;
    }

    public final long count() {
        return ((V2) x(CLASSNAMEt.a)).sum();
    }

    public final CLASSNAMEq summaryStatistics() {
        return (CLASSNAMEq) E(CLASSNAMEd.a, CLASSNAMEf.a, CLASSNAMEl.a);
    }

    public final double H(double identity, r op) {
        return ((Double) w0(V4.a(identity, op))).doubleValue();
    }

    public final B D(r op) {
        return (B) w0(V4.b(op));
    }

    public final Object E(V v, Q q, BiConsumer biConsumer) {
        return w0(V4.c(v, q, new CLASSNAMEq(biConsumer)));
    }

    public final boolean Z(CLASSNAMEv predicate) {
        return ((Boolean) w0(CLASSNAMEf3.e(predicate, CLASSNAMEc3.ANY))).booleanValue();
    }

    public final boolean Q(CLASSNAMEv predicate) {
        return ((Boolean) w0(CLASSNAMEf3.e(predicate, CLASSNAMEc3.ALL))).booleanValue();
    }

    public final boolean K(CLASSNAMEv predicate) {
        return ((Boolean) w0(CLASSNAMEf3.e(predicate, CLASSNAMEc3.NONE))).booleanValue();
    }

    public final B findFirst() {
        return (B) w0(U1.a(true));
    }

    public final B findAny() {
        return (B) w0(U1.a(false));
    }

    static /* synthetic */ Double[] Z0(int x$0) {
        return new Double[x$0];
    }

    public final double[] toArray() {
        return (double[]) CLASSNAMEp4.o((CLASSNAMEm3) x0(CLASSNAMEu.a)).i();
    }
}
