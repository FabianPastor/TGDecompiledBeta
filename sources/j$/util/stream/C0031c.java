package j$.util.stream;

import j$.util.function.m;
import j$.util.y;

/* renamed from: j$.util.stream.c  reason: case insensitive filesystem */
abstract class CLASSNAMEc extends CLASSNAMEz2 implements CLASSNAMEg {
    private final CLASSNAMEc a;
    private final CLASSNAMEc b;
    protected final int c;
    private CLASSNAMEc d;
    private int e;
    private int f;
    private y g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    CLASSNAMEc(CLASSNAMEc cVar, int i2) {
        if (!cVar.h) {
            cVar.h = true;
            cVar.d = this;
            this.b = cVar;
            this.c = CLASSNAMEe4.h & i2;
            this.f = CLASSNAMEe4.a(i2, cVar.f);
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

    CLASSNAMEc(y yVar, int i2, boolean z) {
        this.b = null;
        this.g = yVar;
        this.a = this;
        int i3 = CLASSNAMEe4.g & i2;
        this.c = i3;
        this.f = ((i3 << 1) ^ -1) & CLASSNAMEe4.l;
        this.e = 0;
        this.k = z;
    }

    private y I0(int i2) {
        int i3;
        int i4;
        CLASSNAMEc cVar = this.a;
        y yVar = cVar.g;
        if (yVar != null) {
            cVar.g = null;
            if (cVar.k && cVar.i) {
                CLASSNAMEc cVar2 = cVar.d;
                int i5 = 1;
                while (cVar != this) {
                    int i6 = cVar2.c;
                    if (cVar2.G0()) {
                        i5 = 0;
                        if (CLASSNAMEe4.SHORT_CIRCUIT.d(i6)) {
                            i6 &= CLASSNAMEe4.u ^ -1;
                        }
                        yVar = cVar2.F0(cVar, yVar);
                        if (yVar.hasCharacteristics(64)) {
                            i4 = i6 & (CLASSNAMEe4.t ^ -1);
                            i3 = CLASSNAMEe4.s;
                        } else {
                            i4 = i6 & (CLASSNAMEe4.s ^ -1);
                            i3 = CLASSNAMEe4.t;
                        }
                        i6 = i4 | i3;
                    }
                    cVar2.e = i5;
                    cVar2.f = CLASSNAMEe4.a(i6, cVar.f);
                    i5++;
                    CLASSNAMEc cVar3 = cVar2;
                    cVar2 = cVar2.d;
                    cVar = cVar3;
                }
            }
            if (i2 != 0) {
                this.f = CLASSNAMEe4.a(i2, this.f);
            }
            return yVar;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract void A0(y yVar, CLASSNAMEn3 n3Var);

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEf4 B0();

    /* access modifiers changed from: package-private */
    public final boolean C0() {
        return CLASSNAMEe4.ORDERED.d(this.f);
    }

    public /* synthetic */ y D0() {
        return I0(0);
    }

    /* access modifiers changed from: package-private */
    public B1 E0(CLASSNAMEz2 z2Var, y yVar, m mVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    /* access modifiers changed from: package-private */
    public y F0(CLASSNAMEz2 z2Var, y yVar) {
        return E0(z2Var, yVar, CLASSNAMEa.a).spliterator();
    }

    /* access modifiers changed from: package-private */
    public abstract boolean G0();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEn3 H0(int i2, CLASSNAMEn3 n3Var);

    /* access modifiers changed from: package-private */
    public final y J0() {
        CLASSNAMEc cVar = this.a;
        if (this != cVar) {
            throw new IllegalStateException();
        } else if (!this.h) {
            this.h = true;
            y yVar = cVar.g;
            if (yVar != null) {
                cVar.g = null;
                return yVar;
            }
            throw new IllegalStateException("source already consumed or closed");
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    /* access modifiers changed from: package-private */
    public abstract y K0(CLASSNAMEz2 z2Var, j$.util.function.y yVar, boolean z);

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
    public final void n0(CLASSNAMEn3 n3Var, y yVar) {
        n3Var.getClass();
        if (!CLASSNAMEe4.SHORT_CIRCUIT.d(this.f)) {
            n3Var.n(yVar.getExactSizeIfKnown());
            yVar.forEachRemaining(n3Var);
            n3Var.m();
            return;
        }
        o0(n3Var, yVar);
    }

    /* access modifiers changed from: package-private */
    public final void o0(CLASSNAMEn3 n3Var, y yVar) {
        CLASSNAMEc cVar = this;
        while (cVar.e > 0) {
            cVar = cVar.b;
        }
        n3Var.n(yVar.getExactSizeIfKnown());
        cVar.A0(yVar, n3Var);
        n3Var.m();
    }

    public CLASSNAMEg onClose(Runnable runnable) {
        CLASSNAMEc cVar = this.a;
        Runnable runnable2 = cVar.j;
        if (runnable2 != null) {
            runnable = new N4(runnable2, runnable);
        }
        cVar.j = runnable;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final B1 p0(y yVar, boolean z, m mVar) {
        if (this.a.k) {
            return z0(this, yVar, z, mVar);
        }
        CLASSNAMEt1 t0 = t0(q0(yVar), mVar);
        t0.getClass();
        n0(v0(t0), yVar);
        return t0.a();
    }

    public final CLASSNAMEg parallel() {
        this.a.k = true;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final long q0(y yVar) {
        if (CLASSNAMEe4.SIZED.d(this.f)) {
            return yVar.getExactSizeIfKnown();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEf4 r0() {
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

    public y spliterator() {
        if (!this.h) {
            this.h = true;
            CLASSNAMEc cVar = this.a;
            if (this != cVar) {
                return K0(this, new CLASSNAMEb(this), cVar.k);
            }
            y yVar = cVar.g;
            if (yVar != null) {
                cVar.g = null;
                return yVar;
            }
            throw new IllegalStateException("source already consumed or closed");
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEn3 u0(CLASSNAMEn3 n3Var, y yVar) {
        n3Var.getClass();
        n0(v0(n3Var), yVar);
        return n3Var;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEn3 v0(CLASSNAMEn3 n3Var) {
        n3Var.getClass();
        for (CLASSNAMEc cVar = this; cVar.e > 0; cVar = cVar.b) {
            n3Var = cVar.H0(cVar.b.f, n3Var);
        }
        return n3Var;
    }

    /* access modifiers changed from: package-private */
    public final y w0(y yVar) {
        if (this.e == 0) {
            return yVar;
        }
        return K0(this, new CLASSNAMEb(yVar), this.a.k);
    }

    /* access modifiers changed from: package-private */
    public final Object x0(O4 o4) {
        if (!this.h) {
            this.h = true;
            return this.a.k ? o4.c(this, I0(o4.b())) : o4.d(this, I0(o4.b()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final B1 y0(m mVar) {
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
    public abstract B1 z0(CLASSNAMEz2 z2Var, y yVar, boolean z, m mVar);
}
