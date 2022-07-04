package j$.util.stream;

import j$.util.function.m;
import j$.util.u;

final class A3 extends CLASSNAMEd {
    private final CLASSNAMEc j;
    private final m k;
    private final long l;
    private final long m;
    private long n;
    private volatile boolean o;

    A3(A3 a3, u uVar) {
        super((CLASSNAMEd) a3, uVar);
        this.j = a3.j;
        this.k = a3.k;
        this.l = a3.l;
        this.m = a3.m;
    }

    A3(CLASSNAMEc cVar, CLASSNAMEy2 y2Var, u uVar, m mVar, long j2, long j3) {
        super(y2Var, uVar);
        this.j = cVar;
        this.k = mVar;
        this.l = j2;
        this.m = j3;
    }

    private long m(long j2) {
        if (this.o) {
            return this.n;
        }
        A3 a3 = (A3) this.d;
        A3 a32 = (A3) this.e;
        if (a3 == null || a32 == null) {
            return this.n;
        }
        long m2 = a3.m(j2);
        return m2 >= j2 ? m2 : m2 + a32.m(j2);
    }

    /* access modifiers changed from: protected */
    public Object a() {
        long j2 = -1;
        if (e()) {
            if (CLASSNAMEd4.SIZED.e(this.j.c)) {
                j2 = this.j.q0(this.b);
            }
            CLASSNAMEs1 t0 = this.j.t0(j2, this.k);
            CLASSNAMEm3 H0 = this.j.H0(this.a.s0(), t0);
            CLASSNAMEy2 y2Var = this.a;
            y2Var.o0(y2Var.v0(H0), this.b);
            return t0.a();
        }
        CLASSNAMEy2 y2Var2 = this.a;
        CLASSNAMEs1 t02 = y2Var2.t0(-1, this.k);
        y2Var2.u0(t02, this.b);
        A1 a = t02.a();
        this.n = a.count();
        this.o = true;
        this.b = null;
        return a;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(u uVar) {
        return new A3(this, uVar);
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
    public final A1 k() {
        return CLASSNAMEx2.k(this.j.B0());
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
            j$.util.stream.f r12 = r11.d
            j$.util.stream.A3 r12 = (j$.util.stream.A3) r12
            long r3 = r12.n
            j$.util.stream.f r12 = r11.e
            j$.util.stream.A3 r12 = (j$.util.stream.A3) r12
            long r5 = r12.n
            long r3 = r3 + r5
            r11.n = r3
            boolean r12 = r11.i
            if (r12 == 0) goto L_0x0024
            r11.n = r1
        L_0x001e:
            j$.util.stream.A1 r12 = r11.k()
        L_0x0022:
            r3 = r12
            goto L_0x005f
        L_0x0024:
            long r3 = r11.n
            int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r12 != 0) goto L_0x002b
            goto L_0x001e
        L_0x002b:
            j$.util.stream.f r12 = r11.d
            j$.util.stream.A3 r12 = (j$.util.stream.A3) r12
            long r3 = r12.n
            int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r12 != 0) goto L_0x0040
            j$.util.stream.f r12 = r11.e
            j$.util.stream.A3 r12 = (j$.util.stream.A3) r12
            java.lang.Object r12 = r12.b()
            j$.util.stream.A1 r12 = (j$.util.stream.A1) r12
            goto L_0x0022
        L_0x0040:
            j$.util.stream.c r12 = r11.j
            j$.util.stream.e4 r12 = r12.B0()
            j$.util.stream.f r3 = r11.d
            j$.util.stream.A3 r3 = (j$.util.stream.A3) r3
            java.lang.Object r3 = r3.b()
            j$.util.stream.A1 r3 = (j$.util.stream.A1) r3
            j$.util.stream.f r4 = r11.e
            j$.util.stream.A3 r4 = (j$.util.stream.A3) r4
            java.lang.Object r4 = r4.b()
            j$.util.stream.A1 r4 = (j$.util.stream.A1) r4
            j$.util.stream.A1 r12 = j$.util.stream.CLASSNAMEx2.i(r12, r3, r4)
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
            j$.util.function.m r8 = r11.k
            j$.util.stream.A1 r3 = r3.r(r4, r6, r8)
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
            j$.util.stream.f r12 = r11.c()
            j$.util.stream.A3 r12 = (j$.util.stream.A3) r12
            r5 = r11
        L_0x00b1:
            if (r12 == 0) goto L_0x00d1
            j$.util.stream.f r6 = r12.e
            if (r5 != r6) goto L_0x00c7
            j$.util.stream.f r5 = r12.d
            j$.util.stream.A3 r5 = (j$.util.stream.A3) r5
            if (r5 == 0) goto L_0x00c7
            long r5 = r5.m(r1)
            long r3 = r3 + r5
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c7
            goto L_0x00d7
        L_0x00c7:
            j$.util.stream.f r5 = r12.c()
            j$.util.stream.A3 r5 = (j$.util.stream.A3) r5
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.A3.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
