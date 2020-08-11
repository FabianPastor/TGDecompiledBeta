package j$.util.stream;

import j$.util.B;
import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.Spliterator;
import j$.util.U;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.K;
import j$.util.function.L;
import j$.util.function.M;
import j$.util.function.N;
import j$.util.function.P;
import j$.util.function.T;
import j$.util.function.V;
import j$.util.k0;

abstract class V2 extends CLASSNAMEh1 implements W2 {
    public static /* synthetic */ long Q0(long j, long j2) {
        return j + j2;
    }

    public /* bridge */ /* synthetic */ W2 parallel() {
        super.parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ W2 sequential() {
        super.sequential();
        return this;
    }

    V2(Spliterator spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    V2(CLASSNAMEh1 upstream, int opFlags) {
        super(upstream, opFlags);
    }

    private static J P0(G5 sink) {
        if (sink instanceof J) {
            return (J) sink;
        }
        if (!h7.a) {
            sink.getClass();
            return new CLASSNAMEf1(sink);
        }
        h7.b(CLASSNAMEh1.class, "using LongStream.adapt(Sink<Long> s)");
        throw null;
    }

    /* access modifiers changed from: private */
    public static U O0(Spliterator spliterator) {
        if (spliterator instanceof U) {
            return (U) spliterator;
        }
        if (h7.a) {
            h7.b(CLASSNAMEh1.class, "using LongStream.adapt(Spliterator<Long> s)");
            throw null;
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEv6 A0() {
        return CLASSNAMEv6.LONG_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt3 y0(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree, C c) {
        return CLASSNAMEp4.i(helper, spliterator, flattenTree);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator M0(CLASSNAMEq4 ph, V v, boolean isParallel) {
        return new M6(ph, v, isParallel);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: Y0 */
    public final U F0(V v) {
        return new G6(v);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0012, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r4, j$.util.stream.G5 r5) {
        /*
            r3 = this;
            j$.util.U r0 = O0(r4)
            j$.util.function.J r1 = P0(r5)
        L_0x0008:
            boolean r2 = r5.u()
            if (r2 != 0) goto L_0x0014
            boolean r2 = r0.i(r1)
            if (r2 != 0) goto L_0x0008
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.V2.z0(j$.util.Spliterator, j$.util.stream.G5):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEk3 s0(long exactSizeIfKnown, C c) {
        return CLASSNAMEp4.u(exactSizeIfKnown);
    }

    public final j$.util.J iterator() {
        return k0.h(spliterator());
    }

    public final U spliterator() {
        return O0(super.spliterator());
    }

    public final M1 asDoubleStream() {
        return new C2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s);
    }

    public final Stream boxed() {
        return P(Z0.a);
    }

    public final W2 y(P mapper) {
        mapper.getClass();
        return new E2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final Stream P(K k) {
        k.getClass();
        return new G2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, k);
    }

    public final A2 O(N mapper) {
        mapper.getClass();
        return new I2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final M1 q(M mapper) {
        mapper.getClass();
        return new K2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final W2 t(K k) {
        return new M2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, k);
    }

    /* renamed from: Z0 */
    public W2 unordered() {
        if (!B0()) {
            return this;
        }
        return new N2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.w);
    }

    public final W2 j0(L predicate) {
        predicate.getClass();
        return new P2(this, this, CLASSNAMEv6.LONG_VALUE, CLASSNAMEu6.y, predicate);
    }

    public final W2 s(J action) {
        action.getClass();
        return new R2(this, this, CLASSNAMEv6.LONG_VALUE, 0, action);
    }

    public final W2 limit(long maxSize) {
        if (maxSize >= 0) {
            return Q5.l(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final W2 skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return Q5.l(this, n, -1);
        }
    }

    public final W2 sorted() {
        return CLASSNAMEh6.c(this);
    }

    public final W2 distinct() {
        return ((CLASSNAMEu5) ((CLASSNAMEu5) boxed()).distinct()).h0(Z.a);
    }

    public void i(J action) {
        w0(CLASSNAMEc2.c(action, false));
    }

    public void b0(J action) {
        w0(CLASSNAMEc2.c(action, true));
    }

    public final long sum() {
        return B(0, Y.a);
    }

    public final D min() {
        return l(CLASSNAMEn0.a);
    }

    public final D max() {
        return l(CLASSNAMEc.a);
    }

    static /* synthetic */ long[] R0() {
        return new long[2];
    }

    public final B average() {
        long[] avg = (long[]) g0(V.a, CLASSNAMEa0.a, CLASSNAMEb0.a);
        if (avg[0] <= 0) {
            return B.a();
        }
        double d = (double) avg[1];
        double d2 = (double) avg[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return B.d(d / d2);
    }

    static /* synthetic */ void S0(long[] ll, long i) {
        ll[0] = ll[0] + 1;
        ll[1] = ll[1] + i;
    }

    static /* synthetic */ void T0(long[] ll, long[] rr) {
        ll[0] = ll[0] + rr[0];
        ll[1] = ll[1] + rr[1];
    }

    static /* synthetic */ long V0() {
        return 1;
    }

    public final long count() {
        return ((V2) y(W.a)).sum();
    }

    public final CLASSNAMEw summaryStatistics() {
        return (CLASSNAMEw) g0(T0.a, K0.a, O.a);
    }

    public final long B(long identity, H op) {
        return ((Long) w0(V4.g(identity, op))).longValue();
    }

    public final D l(H op) {
        return (D) w0(V4.h(op));
    }

    public final Object g0(V v, T t, BiConsumer biConsumer) {
        return w0(V4.i(v, t, new T(biConsumer)));
    }

    public final boolean e0(L predicate) {
        return ((Boolean) w0(CLASSNAMEf3.g(predicate, CLASSNAMEc3.ANY))).booleanValue();
    }

    public final boolean i0(L predicate) {
        return ((Boolean) w0(CLASSNAMEf3.g(predicate, CLASSNAMEc3.ALL))).booleanValue();
    }

    public final boolean f(L predicate) {
        return ((Boolean) w0(CLASSNAMEf3.g(predicate, CLASSNAMEc3.NONE))).booleanValue();
    }

    public final D findFirst() {
        return (D) w0(U1.c(true));
    }

    public final D findAny() {
        return (D) w0(U1.c(false));
    }

    static /* synthetic */ Long[] X0(int x$0) {
        return new Long[x$0];
    }

    public final long[] toArray() {
        return (long[]) CLASSNAMEp4.q((CLASSNAMEq3) x0(X.a)).i();
    }
}
