package j$.util.stream;

import j$.util.CLASSNAMEs;
import j$.util.H;
import j$.util.S;
import j$.util.Spliterator;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.G;
import j$.util.function.V;
import j$.util.function.z;
import j$.util.k0;

/* renamed from: j$.util.stream.z2  reason: case insensitive filesystem */
abstract class CLASSNAMEz2 extends CLASSNAMEh1 implements A2 {
    public static /* synthetic */ int Q0(int i, int i2) {
        return i + i2;
    }

    public /* bridge */ /* synthetic */ A2 parallel() {
        super.parallel();
        return this;
    }

    public /* bridge */ /* synthetic */ A2 sequential() {
        super.sequential();
        return this;
    }

    CLASSNAMEz2(Spliterator spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    CLASSNAMEz2(CLASSNAMEh1 upstream, int opFlags) {
        super(upstream, opFlags);
    }

    private static B P0(G5 sink) {
        if (sink instanceof B) {
            return (B) sink;
        }
        if (!h7.a) {
            sink.getClass();
            return new CLASSNAMEa1(sink);
        }
        h7.b(CLASSNAMEh1.class, "using IntStream.adapt(Sink<Integer> s)");
        throw null;
    }

    /* access modifiers changed from: private */
    public static S O0(Spliterator spliterator) {
        if (spliterator instanceof S) {
            return (S) spliterator;
        }
        if (h7.a) {
            h7.b(CLASSNAMEh1.class, "using IntStream.adapt(Spliterator<Integer> s)");
            throw null;
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEv6 A0() {
        return CLASSNAMEv6.INT_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt3 y0(CLASSNAMEq4 helper, Spliterator spliterator, boolean flattenTree, C c) {
        return CLASSNAMEp4.h(helper, spliterator, flattenTree);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator M0(CLASSNAMEq4 ph, V v, boolean isParallel) {
        return new L6(ph, v, isParallel);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: Y0 */
    public final S F0(V v) {
        return new F6(v);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0012, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r4, j$.util.stream.G5 r5) {
        /*
            r3 = this;
            j$.util.S r0 = O0(r4)
            j$.util.function.B r1 = P0(r5)
        L_0x0008:
            boolean r2 = r5.u()
            if (r2 != 0) goto L_0x0014
            boolean r2 = r0.f(r1)
            if (r2 != 0) goto L_0x0008
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEz2.z0(j$.util.Spliterator, j$.util.stream.G5):void");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEk3 s0(long exactSizeIfKnown, C c) {
        return CLASSNAMEp4.s(exactSizeIfKnown);
    }

    public final H iterator() {
        return k0.g(spliterator());
    }

    public final S spliterator() {
        return O0(super.spliterator());
    }

    public final W2 asLongStream() {
        return new CLASSNAMEg2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s);
    }

    public final M1 asDoubleStream() {
        return new CLASSNAMEi2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s);
    }

    public final Stream boxed() {
        return G(CLASSNAMEb1.a);
    }

    public final A2 z(G mapper) {
        mapper.getClass();
        return new CLASSNAMEk2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final Stream G(C c) {
        c.getClass();
        return new CLASSNAMEm2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, c);
    }

    public final W2 k(F mapper) {
        mapper.getClass();
        return new CLASSNAMEo2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final M1 V(E mapper) {
        mapper.getClass();
        return new CLASSNAMEq2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s, mapper);
    }

    public final A2 N(C c) {
        return new CLASSNAMEs2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.u | CLASSNAMEu6.s | CLASSNAMEu6.y, c);
    }

    /* renamed from: Z0 */
    public A2 unordered() {
        if (!B0()) {
            return this;
        }
        return new CLASSNAMEt2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.w);
    }

    public final A2 a0(D predicate) {
        predicate.getClass();
        return new CLASSNAMEv2(this, this, CLASSNAMEv6.INT_VALUE, CLASSNAMEu6.y, predicate);
    }

    public final A2 d0(B action) {
        action.getClass();
        return new CLASSNAMEf2(this, this, CLASSNAMEv6.INT_VALUE, 0, action);
    }

    public final A2 limit(long maxSize) {
        if (maxSize >= 0) {
            return Q5.k(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final A2 skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return Q5.k(this, n, -1);
        }
    }

    public final A2 sorted() {
        return CLASSNAMEh6.b(this);
    }

    public final A2 distinct() {
        return ((CLASSNAMEu5) ((CLASSNAMEu5) boxed()).distinct()).o(J.a);
    }

    public void S(B action) {
        w0(CLASSNAMEc2.b(action, false));
    }

    public void F(B action) {
        w0(CLASSNAMEc2.b(action, true));
    }

    public final int sum() {
        return L(0, K.a);
    }

    public final j$.util.C min() {
        return c0(CLASSNAMEz.a);
    }

    public final j$.util.C max() {
        return c0(E.a);
    }

    static /* synthetic */ long V0() {
        return 1;
    }

    public final long count() {
        return ((V2) k(I.a)).sum();
    }

    static /* synthetic */ long[] R0() {
        return new long[2];
    }

    public final j$.util.B average() {
        long[] avg = (long[]) l0(H.a, F.a, M.a);
        if (avg[0] <= 0) {
            return j$.util.B.a();
        }
        double d = (double) avg[1];
        double d2 = (double) avg[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return j$.util.B.d(d / d2);
    }

    static /* synthetic */ void S0(long[] ll, int i) {
        ll[0] = ll[0] + 1;
        ll[1] = ll[1] + ((long) i);
    }

    static /* synthetic */ void T0(long[] ll, long[] rr) {
        ll[0] = ll[0] + rr[0];
        ll[1] = ll[1] + rr[1];
    }

    public final CLASSNAMEs summaryStatistics() {
        return (CLASSNAMEs) l0(O0.a, G0.a, L0.a);
    }

    public final int L(int identity, z op) {
        return ((Integer) w0(V4.d(identity, op))).intValue();
    }

    public final j$.util.C c0(z op) {
        return (j$.util.C) w0(V4.e(op));
    }

    public final Object l0(V v, j$.util.function.S s, BiConsumer biConsumer) {
        return w0(V4.f(v, s, new L(biConsumer)));
    }

    public final boolean b(D predicate) {
        return ((Boolean) w0(CLASSNAMEf3.f(predicate, CLASSNAMEc3.ANY))).booleanValue();
    }

    public final boolean M(D predicate) {
        return ((Boolean) w0(CLASSNAMEf3.f(predicate, CLASSNAMEc3.ALL))).booleanValue();
    }

    public final boolean T(D predicate) {
        return ((Boolean) w0(CLASSNAMEf3.f(predicate, CLASSNAMEc3.NONE))).booleanValue();
    }

    public final j$.util.C findFirst() {
        return (j$.util.C) w0(U1.b(true));
    }

    public final j$.util.C findAny() {
        return (j$.util.C) w0(U1.b(false));
    }

    static /* synthetic */ Integer[] X0(int x$0) {
        return new Integer[x$0];
    }

    public final int[] toArray() {
        return (int[]) CLASSNAMEp4.p((CLASSNAMEo3) x0(N.a)).i();
    }
}
