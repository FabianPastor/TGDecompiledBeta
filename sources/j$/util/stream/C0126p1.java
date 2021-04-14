package j$.util.stream;

import a.H;
import a.N;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.G;
import j$.util.function.J;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.s;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.function.x;
import j$.util.m;
import j$.util.p;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEw1;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.D1;
import j$.util.stream.R1;
import j$.util.t;
import j$.util.v;
import java.util.Iterator;

/* renamed from: j$.util.stream.p1  reason: case insensitive filesystem */
abstract class CLASSNAMEp1<E_IN> extends CLASSNAMEh1<E_IN, Double, CLASSNAMEs1> implements CLASSNAMEs1 {

    /* renamed from: j$.util.stream.p1$a */
    class a extends i<Double> {
        final /* synthetic */ u l;

        /* renamed from: j$.util.stream.p1$a$a  reason: collision with other inner class name */
        class CLASSNAMEa extends A2.a<Double> {
            CLASSNAMEa(A2 a2) {
                super(a2);
            }

            public void accept(double d) {
                this.var_a.accept(((N) a.this.l).a(d));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        a(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, u uVar) {
            super(h1Var, u2, i);
            this.l = uVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new CLASSNAMEa(a2);
        }
    }

    /* renamed from: j$.util.stream.p1$b */
    class b extends CLASSNAMEy2.m<Double, U> {
        final /* synthetic */ r l;

        /* renamed from: j$.util.stream.p1$b$a */
        class a extends A2.a<U> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(double d) {
                this.var_a.accept(b.this.l.apply(d));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        b(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, r rVar) {
            super(h1Var, u2, i);
            this.l = rVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.p1$c */
    class c extends D1.i<Double> {
        final /* synthetic */ t l;

        /* renamed from: j$.util.stream.p1$c$a */
        class a extends A2.a<Long> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(double d) {
                this.var_a.accept(c.this.l.applyAsLong(d));
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        c(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, t tVar) {
            super(h1Var, u2, i);
            this.l = tVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.p1$d */
    class d extends i<Double> {
        final /* synthetic */ r l;

        /* renamed from: j$.util.stream.p1$d$a */
        class a extends A2.a<Double> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(double d) {
                CLASSNAMEs1 s1Var = (CLASSNAMEs1) d.this.l.apply(d);
                if (s1Var != null) {
                    try {
                        s1Var.sequential().l(new CLASSNAMEo(this));
                    } catch (Throwable unused) {
                    }
                }
                if (s1Var != null) {
                    s1Var.close();
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
        d(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, r rVar) {
            super(h1Var, u2, i);
            this.l = rVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.p1$e */
    class e extends i<Double> {
        final /* synthetic */ s l;

        /* renamed from: j$.util.stream.p1$e$a */
        class a extends A2.a<Double> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(double d) {
                if (((H) e.this.l).b(d)) {
                    this.var_a.accept(d);
                }
            }

            public void n(long j) {
                this.var_a.n(-1);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        e(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, s sVar) {
            super(h1Var, u2, i);
            this.l = sVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.p1$f */
    class f extends i<Double> {
        final /* synthetic */ q l;

        /* renamed from: j$.util.stream.p1$f$a */
        class a extends A2.a<Double> {
            a(A2 a2) {
                super(a2);
            }

            public void accept(double d) {
                f.this.l.accept(d);
                this.var_a.accept(d);
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        f(CLASSNAMEp1 p1Var, CLASSNAMEh1 h1Var, U2 u2, int i, q qVar) {
            super(h1Var, u2, i);
            this.l = qVar;
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }
    }

    /* renamed from: j$.util.stream.p1$g */
    static class g<E_IN> extends CLASSNAMEp1<E_IN> {
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

        public void k0(q qVar) {
            if (!isParallel()) {
                CLASSNAMEp1.L0(I0()).e(qVar);
                return;
            }
            qVar.getClass();
            w0(new CLASSNAMEw1.a(qVar, true));
        }

        public void l(q qVar) {
            if (!isParallel()) {
                CLASSNAMEp1.L0(I0()).e(qVar);
            } else {
                CLASSNAMEp1.super.l(qVar);
            }
        }

        public /* bridge */ /* synthetic */ CLASSNAMEs1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ CLASSNAMEs1 sequential() {
            sequential();
            return this;
        }
    }

    /* renamed from: j$.util.stream.p1$h */
    static abstract class h<E_IN> extends CLASSNAMEp1<E_IN> {
        h(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return true;
        }

        public /* bridge */ /* synthetic */ CLASSNAMEs1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ CLASSNAMEs1 sequential() {
            sequential();
            return this;
        }
    }

    /* renamed from: j$.util.stream.p1$i */
    static abstract class i<E_IN> extends CLASSNAMEp1<E_IN> {
        i(CLASSNAMEh1 h1Var, U2 u2, int i) {
            super(h1Var, i);
        }

        /* access modifiers changed from: package-private */
        public final boolean F0() {
            return false;
        }

        public /* bridge */ /* synthetic */ CLASSNAMEs1 parallel() {
            parallel();
            return this;
        }

        public /* bridge */ /* synthetic */ CLASSNAMEs1 sequential() {
            sequential();
            return this;
        }
    }

    CLASSNAMEp1(Spliterator spliterator, int i2, boolean z) {
        super(spliterator, i2, z);
    }

    CLASSNAMEp1(CLASSNAMEh1 h1Var, int i2) {
        super(h1Var, i2);
    }

    /* access modifiers changed from: private */
    public static Spliterator.a L0(Spliterator spliterator) {
        if (spliterator instanceof Spliterator.a) {
            return (Spliterator.a) spliterator;
        }
        if (i3.var_a) {
            i3.a(CLASSNAMEh1.class, "using DoubleStream.adapt(Spliterator<Double> s)");
            throw null;
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    /* access modifiers changed from: package-private */
    public final U2 A0() {
        return U2.DOUBLE_VALUE;
    }

    public final p C(j$.util.function.p pVar) {
        pVar.getClass();
        return (p) w0(new Y1(U2.DOUBLE_VALUE, pVar));
    }

    public final Object D(J j, G g2, BiConsumer biConsumer) {
        CLASSNAMEw wVar = new CLASSNAMEw(biConsumer);
        j.getClass();
        g2.getClass();
        return w0(new CLASSNAMEa2(U2.DOUBLE_VALUE, wVar, g2, j));
    }

    public final double G(double d2, j$.util.function.p pVar) {
        pVar.getClass();
        return ((Double) w0(new W1(U2.DOUBLE_VALUE, pVar, d2))).doubleValue();
    }

    public final CLASSNAMEs1 H(u uVar) {
        uVar.getClass();
        return new a(this, this, U2.DOUBLE_VALUE, T2.p | T2.n, uVar);
    }

    public final Stream I(r rVar) {
        rVar.getClass();
        return new b(this, this, U2.DOUBLE_VALUE, T2.p | T2.n, rVar);
    }

    public final boolean J(s sVar) {
        return ((Boolean) w0(Q1.r(sVar, N1.NONE))).booleanValue();
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(T1 t1, J j, boolean z) {
        return new Z2(t1, j, z);
    }

    public final boolean O(s sVar) {
        return ((Boolean) w0(Q1.r(sVar, N1.ALL))).booleanValue();
    }

    public final boolean W(s sVar) {
        return ((Boolean) w0(Q1.r(sVar, N1.ANY))).booleanValue();
    }

    public final p average() {
        double[] dArr = (double[]) D(CLASSNAMEx.var_a, CLASSNAMEt.var_a, CLASSNAMEv.var_a);
        return dArr[2] > 0.0d ? p.d(CLASSNAMEn1.a(dArr) / dArr[2]) : p.a();
    }

    public final Stream boxed() {
        return I(O0.var_a);
    }

    public final long count() {
        return ((D1) v(CLASSNAMEs.var_a)).sum();
    }

    public final CLASSNAMEs1 d(q qVar) {
        qVar.getClass();
        return new f(this, this, U2.DOUBLE_VALUE, 0, qVar);
    }

    public final CLASSNAMEs1 distinct() {
        return ((CLASSNAMEy2) ((CLASSNAMEy2) I(O0.var_a)).distinct()).h0(CLASSNAMEn.var_a);
    }

    public final p findAny() {
        return (p) w0(new CLASSNAMEt1(false, U2.DOUBLE_VALUE, p.a(), S0.var_a, U0.var_a));
    }

    public final p findFirst() {
        return (p) w0(new CLASSNAMEt1(true, U2.DOUBLE_VALUE, p.a(), S0.var_a, U0.var_a));
    }

    public final t.a iterator() {
        return v.f(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m27iterator() {
        return v.f(spliterator());
    }

    public void k0(q qVar) {
        qVar.getClass();
        w0(new CLASSNAMEw1.a(qVar, true));
    }

    public void l(q qVar) {
        qVar.getClass();
        w0(new CLASSNAMEw1.a(qVar, false));
    }

    public final CLASSNAMEs1 limit(long j) {
        if (j >= 0) {
            return B2.f(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final p max() {
        return C(D.var_a);
    }

    public final p min() {
        return C(X0.var_a);
    }

    public final C1 o(a.J j) {
        j.getClass();
        return new CLASSNAMEq1(this, this, U2.DOUBLE_VALUE, T2.p | T2.n, j);
    }

    /* access modifiers changed from: package-private */
    public final R1.a s0(long j, x xVar) {
        return S1.j(j);
    }

    public final CLASSNAMEs1 skip(long j) {
        int i2 = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i2 >= 0) {
            return i2 == 0 ? this : B2.f(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEs1 sorted() {
        return new J2(this);
    }

    public final Spliterator.a spliterator() {
        return L0(super.spliterator());
    }

    public final double sum() {
        return CLASSNAMEn1.a((double[]) D(CLASSNAMEu.var_a, r.var_a, CLASSNAMEp.var_a));
    }

    public final m summaryStatistics() {
        return (m) D(H0.var_a, X.var_a, CLASSNAMEk0.var_a);
    }

    public final CLASSNAMEs1 t(s sVar) {
        sVar.getClass();
        return new e(this, this, U2.DOUBLE_VALUE, T2.t, sVar);
    }

    public final double[] toArray() {
        return (double[]) S1.m((R1.b) x0(CLASSNAMEq.var_a)).e();
    }

    public final CLASSNAMEs1 u(r rVar) {
        return new d(this, this, U2.DOUBLE_VALUE, T2.p | T2.n | T2.t, rVar);
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new CLASSNAMEr1(this, this, U2.DOUBLE_VALUE, T2.r);
    }

    public final H1 v(j$.util.function.t tVar) {
        tVar.getClass();
        return new c(this, this, U2.DOUBLE_VALUE, T2.p | T2.n, tVar);
    }

    /* access modifiers changed from: package-private */
    public final R1 y0(T1 t1, Spliterator spliterator, boolean z, x xVar) {
        return S1.f(t1, spliterator, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r3, j$.util.stream.A2 r4) {
        /*
            r2 = this;
            j$.util.Spliterator$a r3 = L0(r3)
            boolean r0 = r4 instanceof j$.util.function.q
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.q r0 = (j$.util.function.q) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.i3.var_a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.M r0 = new j$.util.stream.M
            r0.<init>(r4)
        L_0x0015:
            boolean r1 = r4.p()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.o(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.h1> r3 = j$.util.stream.CLASSNAMEh1.class
            java.lang.String r4 = "using DoubleStream.adapt(Sink<Double> s)"
            j$.util.stream.i3.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEp1.z0(j$.util.Spliterator, j$.util.stream.A2):void");
    }
}
