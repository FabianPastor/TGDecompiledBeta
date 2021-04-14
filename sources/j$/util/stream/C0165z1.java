package j$.util.stream;

import a.CLASSNAMEa0;
import a.CLASSNAMEe0;
import a.Y;
import j$.util.Spliterator;
import j$.util.function.A;
import j$.util.function.BiConsumer;
import j$.util.function.H;
import j$.util.function.J;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.function.x;
import j$.util.function.y;
import j$.util.function.z;
import j$.util.n;
import j$.util.p;
import j$.util.q;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.CLASSNAMEw1;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.D1;
import j$.util.stream.R1;
import j$.util.t;
import java.util.Iterator;

/* renamed from: j$.util.stream.z1  reason: case insensitive filesystem */
abstract class CLASSNAMEz1<E_IN> extends CLASSNAMEh1<E_IN, Integer, C1> implements C1 {

    /* renamed from: j$.util.stream.z1$a */
    class a extends D1.i<Integer> {

        /* renamed from: j$.util.stream.z1$a$a  reason: collision with other inner class name */
        class CLASSNAMEa extends A2.b<Long> {
            CLASSNAMEa(a aVar, A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                this.var_a.accept((long) i);
            }
        }

        a(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, u2, i);
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new CLASSNAMEa(this, a2);
        }
    }

    /* renamed from: j$.util.stream.z1$b */
    class b extends k<Integer> {
        final /* synthetic */ w l;

        /* renamed from: j$.util.stream.z1$b$a */
        class a extends A2.b<Integer> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                b.this.l.accept(i);
                this.var_a.accept(i);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        b(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, w wVar) {
            super(h1Var, u2, i);
            this.l = wVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.z1$c */
    class c extends CLASSNAMEp1.i<Integer> {

        /* renamed from: j$.util.stream.z1$c$a */
        class a extends A2.b<Double> {
            a(c cVar, A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                this.var_a.accept((double) i);
            }
        }

        c(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, u2, i);
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(this, a2);
        }
    }

    /* renamed from: j$.util.stream.z1$d */
    class d extends k<Integer> {
        final /* synthetic */ A l;

        /* renamed from: j$.util.stream.z1$d$a */
        class a extends A2.b<Integer> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                this.var_a.accept(((CLASSNAMEe0) d.this.l).a(i));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        d(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, A a2) {
            super(h1Var, u2, i);
            this.l = a2;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.z1$e */
    class e extends CLASSNAMEy2.m<Integer, U> {
        final /* synthetic */ x l;

        /* renamed from: j$.util.stream.z1$e$a */
        class a extends A2.b<U> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                this.var_a.accept(e.this.l.apply(i));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        e(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, x xVar) {
            super(h1Var, u2, i);
            this.l = xVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.z1$f */
    class f extends D1.i<Integer> {
        final /* synthetic */ z l;

        /* renamed from: j$.util.stream.z1$f$a */
        class a extends A2.b<Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                this.var_a.accept(f.this.l.applyAsLong(i));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        f(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, z zVar) {
            super(h1Var, u2, i);
            this.l = zVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.z1$g */
    class g extends k<Integer> {
        final /* synthetic */ x l;

        /* renamed from: j$.util.stream.z1$g$a */
        class a extends A2.b<Integer> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                C1 c1 = (C1) g.this.l.apply(i);
                if (c1 != null) {
                    try {
                        c1.sequential().Q(new F(this));
                    } catch (Throwable unused) {
                    }
                }
                if (c1 != null) {
                    c1.close();
                    return;
                }
                return;
                throw th;
            }

            public void n(long j) {
                this.var_a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        g(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, x xVar) {
            super(h1Var, u2, i);
            this.l = xVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.z1$h */
    class h extends k<Integer> {
        final /* synthetic */ y l;

        /* renamed from: j$.util.stream.z1$h$a */
        class a extends A2.b<Integer> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(int i) {
                if (((Y) h.this.l).b(i)) {
                    this.var_a.accept(i);
                }
            }

            public void n(long j) {
                this.var_a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        h(CLASSNAMEz1 z1Var, CLASSNAMEh1 h1Var, U2 u2, int i, y yVar) {
            super(h1Var, u2, i);
            this.l = yVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.z1$i */
    static class i<E_IN> extends CLASSNAMEz1<E_IN> {
        i(Spliterator spliterator, int i, boolean z) {
            super(spliterator, i, z);
        }

        public void E(w wVar) {
            if (!isParallel()) {
                CLASSNAMEz1.L0(I0()).c(wVar);
                return;
            }
            wVar.getClass();
            w0(new CLASSNAMEw1.b(wVar, true));
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final A2 G0(int i, A2 a2) {
            throw new UnsupportedOperationException();
        }

        public void Q(w wVar) {
            if (!isParallel()) {
                CLASSNAMEz1.L0(I0()).c(wVar);
            } else {
                CLASSNAMEz1.super.Q(wVar);
            }
        }

        public /* bridge */ /* synthetic */ C1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ C1 sequential() {
            sequential();
            return this;
        }
    }

    /* renamed from: j$.util.stream.z1$j */
    static abstract class j<E_IN> extends CLASSNAMEz1<E_IN> {
        j(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return true;
        }

        public /* bridge */ /* synthetic */ C1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ C1 sequential() {
            sequential();
            return this;
        }
    }

    /* renamed from: j$.util.stream.z1$k */
    static abstract class k<E_IN> extends CLASSNAMEz1<E_IN> {
        k(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return false;
        }

        public /* bridge */ /* synthetic */ C1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ C1 sequential() {
            sequential();
            return this;
        }
    }

    CLASSNAMEz1(Spliterator spliterator, int i2, boolean z) {
        super(spliterator, i2, z);
    }

    CLASSNAMEz1(CLASSNAMEh1 h1Var, int i2) {
        super(h1Var, i2);
    }

    /* access modifiers changed from: private */
    public static Spliterator.b L0(Spliterator spliterator) {
        if (spliterator instanceof Spliterator.b) {
            return (Spliterator.b) spliterator;
        }
        if (i3.var_a) {
            i3.a(CLASSNAMEh1.class, "using IntStream.adapt(Spliterator<Integer> s)");
            throw null;
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    /* access modifiers changed from: package-private */
    public final U2 A0() {
        return U2.INT_VALUE;
    }

    public void E(w wVar) {
        wVar.getClass();
        w0(new CLASSNAMEw1.b(wVar, true));
    }

    public final Stream F(x xVar) {
        xVar.getClass();
        return new e(this, this, U2.INT_VALUE, T2.p | T2.n, xVar);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(T1 t1, J j2, boolean z) {
        return new a3(t1, j2, z);
    }

    public final int K(int i2, v vVar) {
        vVar.getClass();
        return ((Integer) w0(new CLASSNAMEk2(U2.INT_VALUE, vVar, i2))).intValue();
    }

    public final boolean L(y yVar) {
        return ((Boolean) w0(Q1.s(yVar, N1.ALL))).booleanValue();
    }

    public final C1 M(x xVar) {
        return new g(this, this, U2.INT_VALUE, T2.p | T2.n | T2.t, xVar);
    }

    public void Q(w wVar) {
        wVar.getClass();
        w0(new CLASSNAMEw1.b(wVar, false));
    }

    public final boolean R(y yVar) {
        return ((Boolean) w0(Q1.s(yVar, N1.NONE))).booleanValue();
    }

    public final C1 X(y yVar) {
        yVar.getClass();
        return new h(this, this, U2.INT_VALUE, T2.t, yVar);
    }

    public final q Z(v vVar) {
        vVar.getClass();
        return (q) w0(new CLASSNAMEm2(U2.INT_VALUE, vVar));
    }

    public final C1 a0(w wVar) {
        wVar.getClass();
        return new b(this, this, U2.INT_VALUE, 0, wVar);
    }

    public final CLASSNAMEs1 asDoubleStream() {
        return new c(this, this, U2.INT_VALUE, T2.p | T2.n);
    }

    public final H1 asLongStream() {
        return new a(this, this, U2.INT_VALUE, T2.p | T2.n);
    }

    public final p average() {
        long[] jArr = (long[]) j0(E.var_a, L.var_a, K.var_a);
        if (jArr[0] <= 0) {
            return p.a();
        }
        double d2 = (double) jArr[1];
        double d3 = (double) jArr[0];
        Double.isNaN(d2);
        Double.isNaN(d3);
        return p.d(d2 / d3);
    }

    public final boolean b(y yVar) {
        return ((Boolean) w0(Q1.s(yVar, N1.ANY))).booleanValue();
    }

    public final Stream boxed() {
        return F(CLASSNAMEd.var_a);
    }

    public final long count() {
        return ((D1) h(I.var_a)).sum();
    }

    public final C1 distinct() {
        return ((CLASSNAMEy2) ((CLASSNAMEy2) F(CLASSNAMEd.var_a)).distinct()).m(J.var_a);
    }

    public final q findAny() {
        return (q) w0(new CLASSNAMEt1(false, U2.INT_VALUE, q.a(), R0.var_a, W.var_a));
    }

    public final q findFirst() {
        return (q) w0(new CLASSNAMEt1(true, U2.INT_VALUE, q.a(), R0.var_a, W.var_a));
    }

    public final H1 h(z zVar) {
        zVar.getClass();
        return new f(this, this, U2.INT_VALUE, T2.p | T2.n, zVar);
    }

    public final CLASSNAMEs1 i0(CLASSNAMEa0 a0Var) {
        a0Var.getClass();
        return new A1(this, this, U2.INT_VALUE, T2.p | T2.n, a0Var);
    }

    public final t.b iterator() {
        return j$.util.v.g(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m28iterator() {
        return j$.util.v.g(spliterator());
    }

    public final Object j0(J j2, H h2, BiConsumer biConsumer) {
        G g2 = new G(biConsumer);
        j2.getClass();
        h2.getClass();
        return w0(new CLASSNAMEo2(U2.INT_VALUE, g2, h2, j2));
    }

    public final C1 limit(long j2) {
        if (j2 >= 0) {
            return B2.g(this, 0, j2);
        }
        throw new IllegalArgumentException(Long.toString(j2));
    }

    public final q max() {
        return Z(Q0.var_a);
    }

    public final q min() {
        return Z(C.var_a);
    }

    /* access modifiers changed from: package-private */
    public final R1.a s0(long j2, x xVar) {
        return S1.p(j2);
    }

    public final C1 skip(long j2) {
        int i2 = (j2 > 0 ? 1 : (j2 == 0 ? 0 : -1));
        if (i2 >= 0) {
            return i2 == 0 ? this : B2.g(this, j2, -1);
        }
        throw new IllegalArgumentException(Long.toString(j2));
    }

    public final C1 sorted() {
        return new K2(this);
    }

    public final Spliterator.b spliterator() {
        return L0(super.spliterator());
    }

    public final int sum() {
        return ((Integer) w0(new CLASSNAMEk2(U2.INT_VALUE, CLASSNAMEo0.var_a, 0))).intValue();
    }

    public final n summaryStatistics() {
        return (n) j0(CLASSNAMEd1.var_a, CLASSNAMEh.var_a, CLASSNAMEa1.var_a);
    }

    public final int[] toArray() {
        return (int[]) S1.n((R1.c) x0(H.var_a)).e();
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new B1(this, this, U2.INT_VALUE, T2.r);
    }

    public final C1 y(A a2) {
        a2.getClass();
        return new d(this, this, U2.INT_VALUE, T2.p | T2.n, a2);
    }

    /* access modifiers changed from: package-private */
    public final R1 y0(T1 t1, Spliterator spliterator, boolean z, x xVar) {
        return S1.g(t1, spliterator, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r3, j$.util.stream.A2 r4) {
        /*
            r2 = this;
            j$.util.Spliterator$b r3 = L0(r3)
            boolean r0 = r4 instanceof j$.util.function.w
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.w r0 = (j$.util.function.w) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.i3.var_a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.c r0 = new j$.util.stream.c
            r0.<init>(r4)
        L_0x0015:
            boolean r1 = r4.p()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.h(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.h1> r3 = j$.util.stream.CLASSNAMEh1.class
            java.lang.String r4 = "using IntStream.adapt(Sink<Integer> s)"
            j$.util.stream.i3.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEz1.z0(j$.util.Spliterator, j$.util.stream.A2):void");
    }
}
