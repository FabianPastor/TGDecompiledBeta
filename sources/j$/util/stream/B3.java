package j$.util.stream;

import j$.util.function.l;
import j$.util.y;

final class B3 extends CLASSNAMEd {
    private final CLASSNAMEc j;
    private final l k;
    private final long l;
    private final long m;
    private long n;
    private volatile boolean o;

    B3(B3 b3, y yVar) {
        super((CLASSNAMEd) b3, yVar);
        this.j = b3.j;
        this.k = b3.k;
        this.l = b3.l;
        this.m = b3.m;
    }

    B3(CLASSNAMEc cVar, CLASSNAMEz2 z2Var, y yVar, l lVar, long j2, long j3) {
        super(z2Var, yVar);
        this.j = cVar;
        this.k = lVar;
        this.l = j2;
        this.m = j3;
    }

    private long m(long j2) {
        if (this.o) {
            return this.n;
        }
        B3 b3 = (B3) this.d;
        B3 b32 = (B3) this.e;
        if (b3 == null || b32 == null) {
            return this.n;
        }
        long m2 = b3.m(j2);
        return m2 >= j2 ? m2 : m2 + b32.m(j2);
    }

    /* access modifiers changed from: protected */
    public Object a() {
        long j2 = -1;
        if (e()) {
            if (CLASSNAMEe4.SIZED.e(this.j.c)) {
                j2 = this.j.q0(this.b);
            }
            CLASSNAMEt1 t0 = this.j.t0(j2, this.k);
            CLASSNAMEn3 H0 = this.j.H0(this.a.s0(), t0);
            CLASSNAMEz2 z2Var = this.a;
            z2Var.o0(z2Var.v0(H0), this.b);
            return t0.a();
        }
        CLASSNAMEz2 z2Var2 = this.a;
        CLASSNAMEt1 t02 = z2Var2.t0(-1, this.k);
        z2Var2.u0(t02, this.b);
        B1 a = t02.a();
        this.n = a.count();
        this.o = true;
        this.b = null;
        return a;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(y yVar) {
        return new B3(this, yVar);
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
    public final B1 k() {
        return CLASSNAMEy2.k(this.j.B0());
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
            j$.util.stream.B3 r12 = (j$.util.stream.B3) r12
            long r3 = r12.n
            j$.util.stream.f r12 = r11.e
            j$.util.stream.B3 r12 = (j$.util.stream.B3) r12
            long r5 = r12.n
            long r3 = r3 + r5
            r11.n = r3
            boolean r12 = r11.i
            if (r12 == 0) goto L_0x0024
            r11.n = r1
        L_0x001e:
            j$.util.stream.B1 r12 = r11.k()
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
            j$.util.stream.B3 r12 = (j$.util.stream.B3) r12
            long r3 = r12.n
            int r12 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r12 != 0) goto L_0x0040
            j$.util.stream.f r12 = r11.e
            j$.util.stream.B3 r12 = (j$.util.stream.B3) r12
            java.lang.Object r12 = r12.b()
            j$.util.stream.B1 r12 = (j$.util.stream.B1) r12
            goto L_0x0022
        L_0x0040:
            j$.util.stream.c r12 = r11.j
            j$.util.stream.f4 r12 = r12.B0()
            j$.util.stream.f r3 = r11.d
            j$.util.stream.B3 r3 = (j$.util.stream.B3) r3
            java.lang.Object r3 = r3.b()
            j$.util.stream.B1 r3 = (j$.util.stream.B1) r3
            j$.util.stream.f r4 = r11.e
            j$.util.stream.B3 r4 = (j$.util.stream.B3) r4
            java.lang.Object r4 = r4.b()
            j$.util.stream.B1 r4 = (j$.util.stream.B1) r4
            j$.util.stream.B1 r12 = j$.util.stream.CLASSNAMEy2.i(r12, r3, r4)
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
            j$.util.function.l r8 = r11.k
            j$.util.stream.B1 r3 = r3.r(r4, r6, r8)
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
            j$.util.stream.B3 r12 = (j$.util.stream.B3) r12
            r5 = r11
        L_0x00b1:
            if (r12 == 0) goto L_0x00d1
            j$.util.stream.f r6 = r12.e
            if (r5 != r6) goto L_0x00c7
            j$.util.stream.f r5 = r12.d
            j$.util.stream.B3 r5 = (j$.util.stream.B3) r5
            if (r5 == 0) goto L_0x00c7
            long r5 = r5.m(r1)
            long r3 = r3 + r5
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c7
            goto L_0x00d7
        L_0x00c7:
            j$.util.stream.f r5 = r12.c()
            j$.util.stream.B3 r5 = (j$.util.stream.B3) r5
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.B3.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
