package j$.util.stream;

import j$.util.function.m;
import j$.util.function.y;
import j$.util.u;

/* renamed from: j$.util.stream.c  reason: case insensitive filesystem */
abstract class CLASSNAMEc extends CLASSNAMEy2 implements CLASSNAMEg {
    private final CLASSNAMEc a;
    private final CLASSNAMEc b;
    protected final int c;
    private CLASSNAMEc d;
    private int e;
    private int f;
    private u g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    CLASSNAMEc(CLASSNAMEc cVar, int i2) {
        if (!cVar.h) {
            cVar.h = true;
            cVar.d = this;
            this.b = cVar;
            this.c = CLASSNAMEd4.h & i2;
            this.f = CLASSNAMEd4.a(i2, cVar.f);
            CLASSNAMEc cVar2 = cVar.a;
            this.a = cVar2;
            if (G0()) {
                cVar2.i = true;
            }
            this.e = cVar.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    CLASSNAMEc(u uVar, int i2, boolean z) {
        this.b = null;
        this.g = uVar;
        this.a = this;
        int i3 = CLASSNAMEd4.g & i2;
        this.c = i3;
        this.f = ((i3 << 1) ^ -1) & CLASSNAMEd4.l;
        this.e = 0;
        this.k = z;
    }

    private u I0(int i2) {
        int i3;
        int i4;
        CLASSNAMEc cVar = this.a;
        u uVar = cVar.g;
        if (uVar != null) {
            cVar.g = null;
            if (cVar.k && cVar.i) {
                CLASSNAMEc cVar2 = cVar.d;
                int i5 = 1;
                while (cVar != this) {
                    int i6 = cVar2.c;
                    if (cVar2.G0()) {
                        i5 = 0;
                        if (CLASSNAMEd4.SHORT_CIRCUIT.d(i6)) {
                            i6 &= CLASSNAMEd4.u ^ -1;
                        }
                        uVar = cVar2.F0(cVar, uVar);
                        if (uVar.hasCharacteristics(64)) {
                            i4 = i6 & (CLASSNAMEd4.t ^ -1);
                            i3 = CLASSNAMEd4.s;
                        } else {
                            i4 = i6 & (CLASSNAMEd4.s ^ -1);
                            i3 = CLASSNAMEd4.t;
                        }
                        i6 = i4 | i3;
                    }
                    cVar2.e = i5;
                    cVar2.f = CLASSNAMEd4.a(i6, cVar.f);
                    i5++;
                    CLASSNAMEc cVar3 = cVar2;
                    cVar2 = cVar2.d;
                    cVar = cVar3;
                }
            }
            if (i2 != 0) {
                this.f = CLASSNAMEd4.a(i2, this.f);
            }
            return uVar;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract void A0(u uVar, CLASSNAMEm3 m3Var);

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEe4 B0();

    /* access modifiers changed from: package-private */
    public final boolean C0() {
        return CLASSNAMEd4.ORDERED.d(this.f);
    }

    public /* synthetic */ u D0() {
        return I0(0);
    }

    /* access modifiers changed from: package-private */
    public A1 E0(CLASSNAMEy2 y2Var, u uVar, m mVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    /* access modifiers changed from: package-private */
    public u F0(CLASSNAMEy2 y2Var, u uVar) {
        return E0(y2Var, uVar, CLASSNAMEa.a).spliterator();
    }

    /* access modifiers changed from: package-private */
    public abstract boolean G0();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEm3 H0(int i2, CLASSNAMEm3 m3Var);

    /* access modifiers changed from: package-private */
    public final u J0() {
        CLASSNAMEc cVar = this.a;
        if (this != cVar) {
            throw new IllegalStateException();
        } else if (!this.h) {
            this.h = true;
            u uVar = cVar.g;
            if (uVar != null) {
                cVar.g = null;
                return uVar;
            }
            throw new IllegalStateException("source already consumed or closed");
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    /* access modifiers changed from: package-private */
    public abstract u K0(CLASSNAMEy2 y2Var, y yVar, boolean z);

    public void close() {
        this.h = true;
        this.g = null;
        CLASSNAMEc cVar = this.a;
        Runnable runnable = cVar.j;
        if (runnable != null) {
            cVar.j = null;
            runnable.run();
        }
    }

    public final boolean isParallel() {
        return this.a.k;
    }

    /* access modifiers changed from: package-private */
    public final void n0(CLASSNAMEm3 m3Var, u uVar) {
        m3Var.getClass();
        if (!CLASSNAMEd4.SHORT_CIRCUIT.d(this.f)) {
            m3Var.n(uVar.getExactSizeIfKnown());
            uVar.forEachRemaining(m3Var);
            m3Var.m();
            return;
        }
        o0(m3Var, uVar);
    }

    /* access modifiers changed from: package-private */
    public final void o0(CLASSNAMEm3 m3Var, u uVar) {
        CLASSNAMEc cVar = this;
        while (cVar.e > 0) {
            cVar = cVar.b;
        }
        m3Var.n(uVar.getExactSizeIfKnown());
        cVar.A0(uVar, m3Var);
        m3Var.m();
    }

    public CLASSNAMEg onClose(Runnable runnable) {
        CLASSNAMEc cVar = this.a;
        Runnable runnable2 = cVar.j;
        if (runnable2 != null) {
            runnable = new M4(runnable2, runnable);
        }
        cVar.j = runnable;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final A1 p0(u uVar, boolean z, m mVar) {
        if (this.a.k) {
            return z0(this, uVar, z, mVar);
        }
        CLASSNAMEs1 t0 = t0(q0(uVar), mVar);
        t0.getClass();
        n0(v0(t0), uVar);
        return t0.a();
    }

    public final CLASSNAMEg parallel() {
        this.a.k = true;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final long q0(u uVar) {
        if (CLASSNAMEd4.SIZED.d(this.f)) {
            return uVar.getExactSizeIfKnown();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEe4 r0() {
        CLASSNAMEc cVar = this;
        while (cVar.e > 0) {
            cVar = cVar.b;
        }
        return cVar.B0();
    }

    /* access modifiers changed from: package-private */
    public final int s0() {
        return this.f;
    }

    public final CLASSNAMEg sequential() {
        this.a.k = false;
        return this;
    }

    public u spliterator() {
        if (!this.h) {
            this.h = true;
            CLASSNAMEc cVar = this.a;
            if (this != cVar) {
                return K0(this, new CLASSNAMEb(this), cVar.k);
            }
            u uVar = cVar.g;
            if (uVar != null) {
                cVar.g = null;
                return uVar;
            }
            throw new IllegalStateException("source already consumed or closed");
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEm3 u0(CLASSNAMEm3 m3Var, u uVar) {
        m3Var.getClass();
        n0(v0(m3Var), uVar);
        return m3Var;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEm3 v0(CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        for (CLASSNAMEc cVar = this; cVar.e > 0; cVar = cVar.b) {
            m3Var = cVar.H0(cVar.b.f, m3Var);
        }
        return m3Var;
    }

    /* access modifiers changed from: package-private */
    public final u w0(u uVar) {
        if (this.e == 0) {
            return uVar;
        }
        return K0(this, new CLASSNAMEb(uVar), this.a.k);
    }

    /* access modifiers changed from: package-private */
    public final Object x0(N4 n4) {
        if (!this.h) {
            this.h = true;
            return this.a.k ? n4.c(this, I0(n4.b())) : n4.d(this, I0(n4.b()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final A1 y0(m mVar) {
        if (!this.h) {
            this.h = true;
            if (!this.a.k || this.b == null || !G0()) {
                return p0(I0(0), true, mVar);
            }
            this.e = 0;
            CLASSNAMEc cVar = this.b;
            return E0(cVar, cVar.I0(0), mVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract A1 z0(CLASSNAMEy2 y2Var, u uVar, boolean z, m mVar);
}
