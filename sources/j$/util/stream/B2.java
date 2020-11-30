package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.x;
import j$.util.stream.A2;
import j$.util.stream.CLASSNAMEp1;
import j$.util.stream.CLASSNAMEy2;
import j$.util.stream.CLASSNAMEz1;
import j$.util.stream.D1;
import j$.util.stream.R1;
import j$.util.stream.c3;
import j$.util.stream.d3;

final class B2 {

    class a extends CLASSNAMEy2.l<T, T> {
        final /* synthetic */ long l;
        final /* synthetic */ long m;

        /* renamed from: j$.util.stream.B2$a$a  reason: collision with other inner class name */
        class CLASSNAMEa extends A2.d<T, T> {
            long b;
            long c;

            CLASSNAMEa(A2 a2) {
                super(a2);
                this.b = a.this.l;
                long j = a.this.m;
                this.c = j < 0 ? Long.MAX_VALUE : j;
            }

            public void accept(Object obj) {
                long j = this.b;
                if (j == 0) {
                    long j2 = this.c;
                    if (j2 > 0) {
                        this.c = j2 - 1;
                        this.a.accept(obj);
                        return;
                    }
                    return;
                }
                this.b = j - 1;
            }

            public void n(long j) {
                this.a.n(B2.c(j, a.this.l, this.c));
            }

            public boolean p() {
                return this.c == 0 || this.a.p();
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        a(CLASSNAMEh1 h1Var, U2 u2, int i, long j, long j2) {
            super(h1Var, u2, i);
            this.l = j;
            this.m = j2;
        }

        /* access modifiers changed from: package-private */
        public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
            x xVar2 = xVar;
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                T1 t12 = t1;
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                return S1.e(t1, B2.b(t1.q0(), spliterator, this.l, this.m), true, xVar2);
            } else {
                T1 t13 = t1;
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return S1.e(this, K0(t1.v0(spliterator), this.l, this.m, p0), true, xVar2);
            }
            return (R1) new e(this, t1, spliterator, xVar, this.l, this.m).invoke();
        }

        /* access modifiers changed from: package-private */
        public Spliterator E0(T1 t1, Spliterator spliterator) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                Spliterator v0 = t1.v0(spliterator);
                long j = this.l;
                return new c3.e(v0, j, B2.d(j, this.m));
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return K0(t1.v0(spliterator), this.l, this.m, p0);
            }
            return ((R1) new e(this, t1, spliterator, CLASSNAMEu0.a, this.l, this.m).invoke()).spliterator();
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new CLASSNAMEa(a2);
        }

        /* access modifiers changed from: package-private */
        public Spliterator K0(Spliterator spliterator, long j, long j2, long j3) {
            long j4;
            long j5;
            if (j <= j3) {
                long j6 = j3 - j;
                j4 = j2 >= 0 ? Math.min(j2, j6) : j6;
                j5 = 0;
            } else {
                j5 = j;
                j4 = j2;
            }
            return new d3.e(spliterator, j5, j4);
        }
    }

    class b extends CLASSNAMEz1.j<Integer> {
        final /* synthetic */ long l;
        final /* synthetic */ long m;

        class a extends A2.b<Integer> {
            long b;
            long c;

            a(A2 a2) {
                super(a2);
                this.b = b.this.l;
                long j = b.this.m;
                this.c = j < 0 ? Long.MAX_VALUE : j;
            }

            public void accept(int i) {
                long j = this.b;
                if (j == 0) {
                    long j2 = this.c;
                    if (j2 > 0) {
                        this.c = j2 - 1;
                        this.a.accept(i);
                        return;
                    }
                    return;
                }
                this.b = j - 1;
            }

            public void n(long j) {
                this.a.n(B2.c(j, b.this.l, this.c));
            }

            public boolean p() {
                return this.c == 0 || this.a.p();
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        b(CLASSNAMEh1 h1Var, U2 u2, int i, long j, long j2) {
            super(h1Var, u2, i);
            this.l = j;
            this.m = j2;
        }

        /* access modifiers changed from: package-private */
        public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                T1 t12 = t1;
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                return S1.g(t1, B2.b(t1.q0(), spliterator, this.l, this.m), true);
            } else {
                T1 t13 = t1;
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return S1.g(this, M0((Spliterator.b) t1.v0(spliterator), this.l, this.m, p0), true);
            }
            return (R1) new e(this, t1, spliterator, xVar, this.l, this.m).invoke();
        }

        /* access modifiers changed from: package-private */
        public Spliterator E0(T1 t1, Spliterator spliterator) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                long j = this.l;
                return new c3.b((Spliterator.b) t1.v0(spliterator), j, B2.d(j, this.m));
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return M0((Spliterator.b) t1.v0(spliterator), this.l, this.m, p0);
            }
            return ((R1) new e(this, t1, spliterator, CLASSNAMEt0.a, this.l, this.m).invoke()).spliterator();
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }

        /* access modifiers changed from: package-private */
        public Spliterator.b M0(Spliterator.b bVar, long j, long j2, long j3) {
            long j4;
            long j5;
            if (j <= j3) {
                long j6 = j3 - j;
                j4 = j2 >= 0 ? Math.min(j2, j6) : j6;
                j5 = 0;
            } else {
                j5 = j;
                j4 = j2;
            }
            return new d3.b(bVar, j5, j4);
        }
    }

    class c extends D1.h<Long> {
        final /* synthetic */ long l;
        final /* synthetic */ long m;

        class a extends A2.c<Long> {
            long b;
            long c;

            a(A2 a2) {
                super(a2);
                this.b = c.this.l;
                long j = c.this.m;
                this.c = j < 0 ? Long.MAX_VALUE : j;
            }

            public void accept(long j) {
                long j2 = this.b;
                if (j2 == 0) {
                    long j3 = this.c;
                    if (j3 > 0) {
                        this.c = j3 - 1;
                        this.a.accept(j);
                        return;
                    }
                    return;
                }
                this.b = j2 - 1;
            }

            public void n(long j) {
                this.a.n(B2.c(j, c.this.l, this.c));
            }

            public boolean p() {
                return this.c == 0 || this.a.p();
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        c(CLASSNAMEh1 h1Var, U2 u2, int i, long j, long j2) {
            super(h1Var, u2, i);
            this.l = j;
            this.m = j2;
        }

        /* access modifiers changed from: package-private */
        public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                T1 t12 = t1;
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                return S1.h(t1, B2.b(t1.q0(), spliterator, this.l, this.m), true);
            } else {
                T1 t13 = t1;
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return S1.h(this, M0((Spliterator.c) t1.v0(spliterator), this.l, this.m, p0), true);
            }
            return (R1) new e(this, t1, spliterator, xVar, this.l, this.m).invoke();
        }

        /* access modifiers changed from: package-private */
        public Spliterator E0(T1 t1, Spliterator spliterator) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                long j = this.l;
                return new c3.c((Spliterator.c) t1.v0(spliterator), j, B2.d(j, this.m));
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return M0((Spliterator.c) t1.v0(spliterator), this.l, this.m, p0);
            }
            return ((R1) new e(this, t1, spliterator, CLASSNAMEv0.a, this.l, this.m).invoke()).spliterator();
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }

        /* access modifiers changed from: package-private */
        public Spliterator.c M0(Spliterator.c cVar, long j, long j2, long j3) {
            long j4;
            long j5;
            if (j <= j3) {
                long j6 = j3 - j;
                j4 = j2 >= 0 ? Math.min(j2, j6) : j6;
                j5 = 0;
            } else {
                j5 = j;
                j4 = j2;
            }
            return new d3.c(cVar, j5, j4);
        }
    }

    class d extends CLASSNAMEp1.h<Double> {
        final /* synthetic */ long l;
        final /* synthetic */ long m;

        class a extends A2.a<Double> {
            long b;
            long c;

            a(A2 a2) {
                super(a2);
                this.b = d.this.l;
                long j = d.this.m;
                this.c = j < 0 ? Long.MAX_VALUE : j;
            }

            public void accept(double d2) {
                long j = this.b;
                if (j == 0) {
                    long j2 = this.c;
                    if (j2 > 0) {
                        this.c = j2 - 1;
                        this.a.accept(d2);
                        return;
                    }
                    return;
                }
                this.b = j - 1;
            }

            public void n(long j) {
                this.a.n(B2.c(j, d.this.l, this.c));
            }

            public boolean p() {
                return this.c == 0 || this.a.p();
            }
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        d(CLASSNAMEh1 h1Var, U2 u2, int i, long j, long j2) {
            super(h1Var, u2, i);
            this.l = j;
            this.m = j2;
        }

        /* access modifiers changed from: package-private */
        public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                T1 t12 = t1;
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                return S1.f(t1, B2.b(t1.q0(), spliterator, this.l, this.m), true);
            } else {
                T1 t13 = t1;
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return S1.f(this, M0((Spliterator.a) t1.v0(spliterator), this.l, this.m, p0), true);
            }
            return (R1) new e(this, t1, spliterator, xVar, this.l, this.m).invoke();
        }

        /* access modifiers changed from: package-private */
        public Spliterator E0(T1 t1, Spliterator spliterator) {
            long p0 = t1.p0(spliterator);
            if (p0 <= 0) {
                Spliterator spliterator2 = spliterator;
            } else if (spliterator.hasCharacteristics(16384)) {
                long j = this.l;
                return new c3.a((Spliterator.a) t1.v0(spliterator), j, B2.d(j, this.m));
            }
            if (!T2.ORDERED.d(t1.r0())) {
                return M0((Spliterator.a) t1.v0(spliterator), this.l, this.m, p0);
            }
            return ((R1) new e(this, t1, spliterator, CLASSNAMEw0.a, this.l, this.m).invoke()).spliterator();
        }

        /* access modifiers changed from: package-private */
        public A2 G0(int i, A2 a2) {
            return new a(a2);
        }

        /* access modifiers changed from: package-private */
        public Spliterator.a M0(Spliterator.a aVar, long j, long j2, long j3) {
            long j4;
            long j5;
            if (j <= j3) {
                long j6 = j3 - j;
                j4 = j2 >= 0 ? Math.min(j2, j6) : j6;
                j5 = 0;
            } else {
                j5 = j;
                j4 = j2;
            }
            return new d3.a(aVar, j5, j4);
        }
    }

    private static final class e<P_IN, P_OUT> extends CLASSNAMEi1<P_IN, P_OUT, R1<P_OUT>, e<P_IN, P_OUT>> {
        private final CLASSNAMEh1 j;
        private final x k;
        private final long l;
        private final long m;
        private long n;
        private volatile boolean o;

        e(e eVar, Spliterator spliterator) {
            super((CLASSNAMEi1) eVar, spliterator);
            this.j = eVar.j;
            this.k = eVar.k;
            this.l = eVar.l;
            this.m = eVar.m;
        }

        e(CLASSNAMEh1 h1Var, T1 t1, Spliterator spliterator, x xVar, long j2, long j3) {
            super(t1, spliterator);
            this.j = h1Var;
            this.k = xVar;
            this.l = j2;
            this.m = j3;
        }

        private long m(long j2) {
            if (this.o) {
                return this.n;
            }
            e eVar = (e) this.d;
            e eVar2 = (e) this.e;
            if (eVar == null || eVar2 == null) {
                return this.n;
            }
            long m2 = eVar.m(j2);
            return m2 >= j2 ? m2 : m2 + eVar2.m(j2);
        }

        /* access modifiers changed from: protected */
        public Object a() {
            long j2 = -1;
            if (e()) {
                if (T2.SIZED.e(this.j.c)) {
                    j2 = this.j.p0(this.b);
                }
                R1.a s0 = this.j.s0(j2, this.k);
                A2 G0 = this.j.G0(this.a.r0(), s0);
                T1 t1 = this.a;
                t1.n0(t1.u0(G0), this.b);
                return s0.a();
            }
            T1 t12 = this.a;
            R1.a s02 = t12.s0(-1, this.k);
            t12.t0(s02, this.b);
            R1 a = s02.a();
            this.n = a.count();
            this.o = true;
            this.b = null;
            return a;
        }

        /* access modifiers changed from: protected */
        public CLASSNAMEk1 f(Spliterator spliterator) {
            return new e(this, spliterator);
        }

        /* access modifiers changed from: protected */
        public void i() {
            this.i = true;
            if (this.o) {
                g(k());
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: n */
        public final R1 k() {
            return S1.k(this.j.A0());
        }

        /* JADX WARNING: Removed duplicated region for block: B:15:0x0065  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void onCompletion(java.util.concurrent.CountedCompleter r12) {
            /*
                r11 = this;
                boolean r12 = r11.d()
                r0 = 1
                r1 = 0
                if (r12 != 0) goto L_0x0089
                j$.util.stream.k1 r12 = r11.d
                j$.util.stream.B2$e r12 = (j$.util.stream.B2.e) r12
                long r3 = r12.n
                j$.util.stream.k1 r12 = r11.e
                j$.util.stream.B2$e r12 = (j$.util.stream.B2.e) r12
                long r5 = r12.n
                long r3 = r3 + r5
                r11.n = r3
                boolean r12 = r11.i
                if (r12 == 0) goto L_0x0024
                r11.n = r1
            L_0x001e:
                j$.util.stream.R1 r12 = r11.k()
            L_0x0022:
                r3 = r12
                goto L_0x005f
            L_0x0024:
                long r3 = r11.n
                int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r12 != 0) goto L_0x002b
                goto L_0x001e
            L_0x002b:
                j$.util.stream.k1 r12 = r11.d
                j$.util.stream.B2$e r12 = (j$.util.stream.B2.e) r12
                long r3 = r12.n
                int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r12 != 0) goto L_0x0040
                j$.util.stream.k1 r12 = r11.e
                j$.util.stream.B2$e r12 = (j$.util.stream.B2.e) r12
                java.lang.Object r12 = r12.b()
                j$.util.stream.R1 r12 = (j$.util.stream.R1) r12
                goto L_0x0022
            L_0x0040:
                j$.util.stream.h1 r12 = r11.j
                j$.util.stream.U2 r12 = r12.A0()
                j$.util.stream.k1 r3 = r11.d
                j$.util.stream.B2$e r3 = (j$.util.stream.B2.e) r3
                java.lang.Object r3 = r3.b()
                j$.util.stream.R1 r3 = (j$.util.stream.R1) r3
                j$.util.stream.k1 r4 = r11.e
                j$.util.stream.B2$e r4 = (j$.util.stream.B2.e) r4
                java.lang.Object r4 = r4.b()
                j$.util.stream.R1 r4 = (j$.util.stream.R1) r4
                j$.util.stream.R1 r12 = j$.util.stream.S1.i(r12, r3, r4)
                goto L_0x0022
            L_0x005f:
                boolean r12 = r11.e()
                if (r12 == 0) goto L_0x0084
                long r4 = r11.m
                int r12 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
                if (r12 < 0) goto L_0x0079
                long r4 = r3.count()
                long r6 = r11.l
                long r8 = r11.m
                long r6 = r6 + r8
                long r4 = java.lang.Math.min(r4, r6)
                goto L_0x007b
            L_0x0079:
                long r4 = r11.n
            L_0x007b:
                r6 = r4
                long r4 = r11.l
                j$.util.function.x r8 = r11.k
                j$.util.stream.R1 r3 = r3.r(r4, r6, r8)
            L_0x0084:
                r11.g(r3)
                r11.o = r0
            L_0x0089:
                long r3 = r11.m
                int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r12 < 0) goto L_0x00dc
                boolean r12 = r11.e()
                if (r12 != 0) goto L_0x00dc
                long r1 = r11.l
                long r3 = r11.m
                long r1 = r1 + r3
                boolean r12 = r11.o
                if (r12 == 0) goto L_0x00a1
                long r3 = r11.n
                goto L_0x00a5
            L_0x00a1:
                long r3 = r11.m(r1)
            L_0x00a5:
                int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r12 < 0) goto L_0x00aa
                goto L_0x00d7
            L_0x00aa:
                j$.util.stream.k1 r12 = r11.c()
                j$.util.stream.B2$e r12 = (j$.util.stream.B2.e) r12
                r5 = r11
            L_0x00b1:
                if (r12 == 0) goto L_0x00d1
                j$.util.stream.k1 r6 = r12.e
                if (r5 != r6) goto L_0x00c7
                j$.util.stream.k1 r5 = r12.d
                j$.util.stream.B2$e r5 = (j$.util.stream.B2.e) r5
                if (r5 == 0) goto L_0x00c7
                long r5 = r5.m(r1)
                long r3 = r3 + r5
                int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r5 < 0) goto L_0x00c7
                goto L_0x00d7
            L_0x00c7:
                j$.util.stream.k1 r5 = r12.c()
                j$.util.stream.B2$e r5 = (j$.util.stream.B2.e) r5
                r10 = r5
                r5 = r12
                r12 = r10
                goto L_0x00b1
            L_0x00d1:
                int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                if (r12 < 0) goto L_0x00d6
                goto L_0x00d7
            L_0x00d6:
                r0 = 0
            L_0x00d7:
                if (r0 == 0) goto L_0x00dc
                r11.j()
            L_0x00dc:
                r12 = 0
                r11.b = r12
                r11.e = r12
                r11.d = r12
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.B2.e.onCompletion(java.util.concurrent.CountedCompleter):void");
        }
    }

    static Spliterator b(U2 u2, Spliterator spliterator, long j, long j2) {
        long d2 = d(j, j2);
        int ordinal = u2.ordinal();
        if (ordinal == 0) {
            return new c3.e(spliterator, j, d2);
        }
        if (ordinal == 1) {
            return new c3.b((Spliterator.b) spliterator, j, d2);
        }
        if (ordinal == 2) {
            return new c3.c((Spliterator.c) spliterator, j, d2);
        }
        if (ordinal == 3) {
            return new c3.a((Spliterator.a) spliterator, j, d2);
        }
        throw new IllegalStateException("Unknown shape " + u2);
    }

    static long c(long j, long j2, long j3) {
        if (j >= 0) {
            return Math.max(-1, Math.min(j - j2, j3));
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public static long d(long j, long j2) {
        long j3 = j2 >= 0 ? j + j2 : Long.MAX_VALUE;
        if (j3 >= 0) {
            return j3;
        }
        return Long.MAX_VALUE;
    }

    private static int e(long j) {
        return (j != -1 ? T2.u : 0) | T2.t;
    }

    public static CLASSNAMEs1 f(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new d(h1Var, U2.DOUBLE_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static C1 g(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new b(h1Var, U2.INT_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static H1 h(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new c(h1Var, U2.LONG_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static Stream i(CLASSNAMEh1 h1Var, long j, long j2) {
        if (j >= 0) {
            return new a(h1Var, U2.REFERENCE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }
}
