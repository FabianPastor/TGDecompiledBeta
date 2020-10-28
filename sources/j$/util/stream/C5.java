package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.v;

final class C5 extends CLASSNAMEi1 {
    private final CLASSNAMEh1 j;
    private final v k;
    private final long l;
    private final long m;
    private long n;
    private volatile boolean o;

    C5(C5 c5, Spliterator spliterator) {
        super((CLASSNAMEi1) c5, spliterator);
        this.j = c5.j;
        this.k = c5.k;
        this.l = c5.l;
        this.m = c5.m;
    }

    C5(CLASSNAMEh1 h1Var, CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar, long j2, long j3) {
        super(i4Var, spliterator);
        this.j = h1Var;
        this.k = vVar;
        this.l = j2;
        this.m = j3;
    }

    private long m(long j2) {
        if (this.o) {
            return this.n;
        }
        C5 c5 = (C5) this.d;
        C5 CLASSNAME = (C5) this.e;
        if (c5 == null || CLASSNAME == null) {
            return this.n;
        }
        long m2 = c5.m(j2);
        return m2 >= j2 ? m2 : m2 + CLASSNAME.m(j2);
    }

    /* access modifiers changed from: protected */
    public Object a() {
        long j2 = -1;
        if (e()) {
            if (CLASSNAMEg6.SIZED.e(this.j.c)) {
                j2 = this.j.p0(this.b);
            }
            CLASSNAMEg3 s0 = this.j.s0(j2, this.k);
            CLASSNAMEt5 G0 = this.j.G0(this.a.r0(), s0);
            CLASSNAMEi4 i4Var = this.a;
            i4Var.n0(i4Var.u0(G0), this.b);
            return s0.a();
        }
        CLASSNAMEi4 i4Var2 = this.a;
        CLASSNAMEg3 s02 = i4Var2.s0(-1, this.k);
        i4Var2.t0(s02, this.b);
        CLASSNAMEl3 a = s02.a();
        this.n = a.count();
        this.o = true;
        this.b = null;
        return a;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk1 f(Spliterator spliterator) {
        return new C5(this, spliterator);
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
    public final CLASSNAMEl3 k() {
        return CLASSNAMEh4.k(this.j.A0());
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
            j$.util.stream.C5 r12 = (j$.util.stream.C5) r12
            long r3 = r12.n
            j$.util.stream.k1 r12 = r11.e
            j$.util.stream.C5 r12 = (j$.util.stream.C5) r12
            long r5 = r12.n
            long r3 = r3 + r5
            r11.n = r3
            boolean r12 = r11.i
            if (r12 == 0) goto L_0x0024
            r11.n = r1
        L_0x001e:
            j$.util.stream.l3 r12 = r11.k()
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
            j$.util.stream.C5 r12 = (j$.util.stream.C5) r12
            long r3 = r12.n
            int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r12 != 0) goto L_0x0040
            j$.util.stream.k1 r12 = r11.e
            j$.util.stream.C5 r12 = (j$.util.stream.C5) r12
            java.lang.Object r12 = r12.b()
            j$.util.stream.l3 r12 = (j$.util.stream.CLASSNAMEl3) r12
            goto L_0x0022
        L_0x0040:
            j$.util.stream.h1 r12 = r11.j
            j$.util.stream.h6 r12 = r12.A0()
            j$.util.stream.k1 r3 = r11.d
            j$.util.stream.C5 r3 = (j$.util.stream.C5) r3
            java.lang.Object r3 = r3.b()
            j$.util.stream.l3 r3 = (j$.util.stream.CLASSNAMEl3) r3
            j$.util.stream.k1 r4 = r11.e
            j$.util.stream.C5 r4 = (j$.util.stream.C5) r4
            java.lang.Object r4 = r4.b()
            j$.util.stream.l3 r4 = (j$.util.stream.CLASSNAMEl3) r4
            j$.util.stream.l3 r12 = j$.util.stream.CLASSNAMEh4.i(r12, r3, r4)
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
            j$.util.function.v r8 = r11.k
            j$.util.stream.l3 r3 = r3.r(r4, r6, r8)
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
            j$.util.stream.C5 r12 = (j$.util.stream.C5) r12
            r5 = r11
        L_0x00b1:
            if (r12 == 0) goto L_0x00d1
            j$.util.stream.k1 r6 = r12.e
            if (r5 != r6) goto L_0x00c7
            j$.util.stream.k1 r5 = r12.d
            j$.util.stream.C5 r5 = (j$.util.stream.C5) r5
            if (r5 == 0) goto L_0x00c7
            long r5 = r5.m(r1)
            long r3 = r3 + r5
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c7
            goto L_0x00d7
        L_0x00c7:
            j$.util.stream.k1 r5 = r12.c()
            j$.util.stream.C5 r5 = (j$.util.stream.C5) r5
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.C5.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
