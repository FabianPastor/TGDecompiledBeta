package j$.util.stream;

import j$.CLASSNAMEj0;
import j$.CLASSNAMEl0;
import j$.CLASSNAMEn0;
import j$.util.Spliterator;
import j$.util.function.B;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.D;
import j$.util.function.E;
import j$.util.function.F;
import j$.util.function.I;
import j$.util.function.J;
import j$.util.function.x;
import j$.util.n;
import j$.util.o;
import j$.util.q;
import j$.util.s;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.CLASSNAMEw1;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.R1;
import j$.util.u;
import java.util.Iterator;

abstract class D1<E_IN> extends CLASSNAMEh1<E_IN, Long, H1> implements H1 {

    class a extends CLASSNAMEp1.i<Long> {

        /* renamed from: j$.util.stream.D1$a$a  reason: collision with other inner class name */
        class CLASSNAMEa extends A2.c<Double> {
            CLASSNAMEa(a aVar, A2 a2) {
                super(a2);
            }

            public void accept(long j) {
                this.a.accept((double) j);
            }
        }

        a(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, u2, i);
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new CLASSNAMEa(this, a2);
        }
    }

    class b extends i<Long> {
        final /* synthetic */ F l;

        class a extends A2.c<Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(long j) {
                this.a.accept(b.this.l.applyAsLong(j));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        b(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, F f) {
            super(h1Var, u2, i);
            this.l = f;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    class c extends CLASSNAMEy2.m<Long, U> {
        final /* synthetic */ D l;

        class a extends A2.c<U> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(long j) {
                this.a.accept(c.this.l.apply(j));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        c(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, D d) {
            super(h1Var, u2, i);
            this.l = d;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    class d extends i<Long> {
        final /* synthetic */ D l;

        class a extends A2.c<Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(long j) {
                H1 h1 = (H1) d.this.l.apply(j);
                if (h1 != null) {
                    try {
                        h1.sequential().f(new P(this));
                    } catch (Throwable unused) {
                    }
                }
                if (h1 != null) {
                    h1.close();
                    return;
                }
                return;
                throw th;
            }

            public void m(long j) {
                this.a.m(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        d(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, D d) {
            super(h1Var, u2, i);
            this.l = d;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    class e extends i<Long> {
        final /* synthetic */ E l;

        class a extends A2.c<Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(long j) {
                if (((CLASSNAMEj0) e.this.l).b(j)) {
                    this.a.accept(j);
                }
            }

            public void m(long j) {
                this.a.m(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        e(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, E e) {
            super(h1Var, u2, i);
            this.l = e;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    class f extends i<Long> {
        final /* synthetic */ C l;

        class a extends A2.c<Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(long j) {
                f.this.l.accept(j);
                this.a.accept(j);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        f(D1 d1, CLASSNAMEh1 h1Var, U2 u2, int i, C c) {
            super(h1Var, u2, i);
            this.l = c;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    static class g<E_IN> extends D1<E_IN> {
        g(Spliterator spliterator, int i, boolean z) {
            super(spliterator, i, z);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final A2 G0(int i, A2 a2) {
            throw new UnsupportedOperationException();
        }

        public void Y(C c) {
            if (!isParallel()) {
                D1.L0(I0()).d(c);
                return;
            }
            c.getClass();
            w0(new CLASSNAMEw1.c(c, true));
        }

        public void f(C c) {
            if (!isParallel()) {
                D1.L0(I0()).d(c);
            } else {
                D1.super.f(c);
            }
        }

        public /* bridge */ /* synthetic */ H1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ H1 sequential() {
            sequential();
            return this;
        }
    }

    static abstract class h<E_IN> extends D1<E_IN> {
        h(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return true;
        }

        public /* bridge */ /* synthetic */ H1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ H1 sequential() {
            sequential();
            return this;
        }
    }

    static abstract class i<E_IN> extends D1<E_IN> {
        i(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return false;
        }

        public /* bridge */ /* synthetic */ H1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ H1 sequential() {
            sequential();
            return this;
        }
    }

    D1(Spliterator spliterator, int i2, boolean z) {
        super(spliterator, i2, z);
    }

    D1(CLASSNAMEh1 h1Var, int i2) {
        super(h1Var, i2);
    }

    /* access modifiers changed from: private */
    public static Spliterator.c L0(Spliterator spliterator) {
        if (spliterator instanceof Spliterator.c) {
            return (Spliterator.c) spliterator;
        }
        if (i3.a) {
            i3.a(CLASSNAMEh1.class, "using LongStream.adapt(Spliterator<Long> s)");
            throw null;
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    public final long A(long j, B b2) {
        b2.getClass();
        return ((Long) w0(new CLASSNAMEq2(U2.LONG_VALUE, b2, j))).longValue();
    }

    /* access modifiers changed from: package-private */
    public final U2 A0() {
        return U2.LONG_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(T1 t1, J j, boolean z) {
        return new b3(t1, j, z);
    }

    public final Stream N(D d2) {
        d2.getClass();
        return new c(this, this, U2.LONG_VALUE, T2.k | T2.i, d2);
    }

    public void Y(C c2) {
        c2.getClass();
        w0(new CLASSNAMEw1.c(c2, true));
    }

    public final CLASSNAMEs1 asDoubleStream() {
        return new a(this, this, U2.LONG_VALUE, T2.k | T2.i);
    }

    public final o average() {
        long[] jArr = (long[]) d0(V.a, O.a, N.a);
        if (jArr[0] <= 0) {
            return o.a();
        }
        double d2 = (double) jArr[1];
        double d3 = (double) jArr[0];
        Double.isNaN(d2);
        Double.isNaN(d3);
        return o.d(d2 / d3);
    }

    public final boolean b0(E e2) {
        return ((Boolean) w0(Q1.t(e2, N1.ANY))).booleanValue();
    }

    public final Stream boxed() {
        return N(CLASSNAMEa.a);
    }

    public final boolean c(E e2) {
        return ((Boolean) w0(Q1.t(e2, N1.NONE))).booleanValue();
    }

    public final long count() {
        return ((D1) x(U.a)).sum();
    }

    public final Object d0(J j, I i2, BiConsumer biConsumer) {
        S s = new S(biConsumer);
        j.getClass();
        i2.getClass();
        return w0(new U1(U2.LONG_VALUE, s, i2, j));
    }

    public final H1 distinct() {
        return ((CLASSNAMEy2) N(CLASSNAMEa.a)).distinct().e0(T.a);
    }

    public void f(C c2) {
        c2.getClass();
        w0(new CLASSNAMEw1.c(c2, false));
    }

    public final boolean f0(E e2) {
        return ((Boolean) w0(Q1.t(e2, N1.ALL))).booleanValue();
    }

    public final q findAny() {
        return (q) w0(new CLASSNAMEt1(false, U2.LONG_VALUE, q.a(), Z0.a, CLASSNAMEe0.a));
    }

    public final q findFirst() {
        return (q) w0(new CLASSNAMEt1(true, U2.LONG_VALUE, q.a(), Z0.a, CLASSNAMEe0.a));
    }

    public final H1 g0(E e2) {
        e2.getClass();
        return new e(this, this, U2.LONG_VALUE, T2.o, e2);
    }

    public final q i(B b2) {
        b2.getClass();
        return (q) w0(new CLASSNAMEs2(U2.LONG_VALUE, b2));
    }

    public final s.c iterator() {
        return u.h(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m0iterator() {
        return u.h(spliterator());
    }

    public final CLASSNAMEs1 j(CLASSNAMEl0 l0Var) {
        l0Var.getClass();
        return new F1(this, this, U2.LONG_VALUE, T2.k | T2.i, l0Var);
    }

    public final H1 limit(long j) {
        if (j >= 0) {
            return B2.h(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final q max() {
        return i(Y0.a);
    }

    public final q min() {
        return i(Y.a);
    }

    public final H1 q(C c2) {
        c2.getClass();
        return new f(this, this, U2.LONG_VALUE, 0, c2);
    }

    public final H1 r(D d2) {
        return new d(this, this, U2.LONG_VALUE, T2.k | T2.i | T2.o, d2);
    }

    /* access modifiers changed from: package-private */
    public final R1.a s0(long j, x xVar) {
        return S1.q(j);
    }

    public final H1 skip(long j) {
        int i2 = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i2 >= 0) {
            return i2 == 0 ? this : B2.h(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final H1 sorted() {
        return new L2(this);
    }

    public final Spliterator.c spliterator() {
        return L0(super.spliterator());
    }

    public final long sum() {
        return ((Long) w0(new CLASSNAMEq2(U2.LONG_VALUE, J0.a, 0))).longValue();
    }

    public final n summaryStatistics() {
        return (n) d0(CLASSNAMEf1.a, CLASSNAMEm0.a, CLASSNAMEw0.a);
    }

    public final long[] toArray() {
        return (long[]) S1.o((R1.d) x0(Q.a)).e();
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new G1(this, this, U2.LONG_VALUE, T2.m);
    }

    public final C1 w(CLASSNAMEn0 n0Var) {
        n0Var.getClass();
        return new E1(this, this, U2.LONG_VALUE, T2.k | T2.i, n0Var);
    }

    public final H1 x(F f2) {
        f2.getClass();
        return new b(this, this, U2.LONG_VALUE, T2.k | T2.i, f2);
    }

    /* access modifiers changed from: package-private */
    public final R1 y0(T1 t1, Spliterator spliterator, boolean z, x xVar) {
        return S1.h(t1, spliterator, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r3, j$.util.stream.A2 r4) {
        /*
            r2 = this;
            j$.util.Spliterator$c r3 = L0(r3)
            boolean r0 = r4 instanceof j$.util.function.C
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.C r0 = (j$.util.function.C) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.i3.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.K0 r0 = new j$.util.stream.K0
            r0.<init>(r4)
        L_0x0015:
            boolean r1 = r4.o()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.i(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.h1> r3 = j$.util.stream.CLASSNAMEh1.class
            java.lang.String r4 = "using LongStream.adapt(Sink<Long> s)"
            j$.util.stream.i3.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.D1.z0(j$.util.Spliterator, j$.util.stream.A2):void");
    }
}
