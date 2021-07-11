package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.J;
import j$.util.function.x;
import j$.util.stream.CLASSNAMEl1;
import j$.util.stream.R1;

/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
abstract class CLASSNAMEh1<E_IN, E_OUT, S extends CLASSNAMEl1<E_OUT, S>> extends T1<E_OUT> implements CLASSNAMEl1<E_OUT, S> {
    private final CLASSNAMEh1 a;
    private final CLASSNAMEh1 b;
    protected final int c;
    private CLASSNAMEh1 d;
    private int e;
    private int f;
    private Spliterator g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    CLASSNAMEh1(Spliterator spliterator, int i2, boolean z) {
        this.b = null;
        this.g = spliterator;
        this.a = this;
        int i3 = T2.b & i2;
        this.c = i3;
        this.f = ((i3 << 1) ^ -1) & T2.g;
        this.e = 0;
        this.k = z;
    }

    CLASSNAMEh1(CLASSNAMEh1 h1Var, int i2) {
        if (!h1Var.h) {
            h1Var.h = true;
            h1Var.d = this;
            this.b = h1Var;
            this.c = T2.c & i2;
            this.f = T2.a(i2, h1Var.f);
            CLASSNAMEh1 h1Var2 = h1Var.a;
            this.a = h1Var2;
            if (F0()) {
                h1Var2.i = true;
            }
            this.e = h1Var.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    private Spliterator H0(int i2) {
        int i3;
        int i4;
        CLASSNAMEh1 h1Var = this.a;
        Spliterator spliterator = h1Var.g;
        if (spliterator != null) {
            h1Var.g = null;
            if (h1Var.k && h1Var.i) {
                CLASSNAMEh1 h1Var2 = h1Var.d;
                int i5 = 1;
                while (h1Var != this) {
                    int i6 = h1Var2.c;
                    if (h1Var2.F0()) {
                        i5 = 0;
                        if (T2.SHORT_CIRCUIT.d(i6)) {
                            i6 &= T2.p ^ -1;
                        }
                        spliterator = h1Var2.E0(h1Var, spliterator);
                        if (spliterator.hasCharacteristics(64)) {
                            i4 = i6 & (T2.o ^ -1);
                            i3 = T2.n;
                        } else {
                            i4 = i6 & (T2.n ^ -1);
                            i3 = T2.o;
                        }
                        i6 = i4 | i3;
                    }
                    h1Var2.e = i5;
                    h1Var2.f = T2.a(i6, h1Var.f);
                    i5++;
                    CLASSNAMEh1 h1Var3 = h1Var2;
                    h1Var2 = h1Var2.d;
                    h1Var = h1Var3;
                }
            }
            if (i2 != 0) {
                this.f = T2.a(i2, this.f);
            }
            return spliterator;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract U2 A0();

    /* access modifiers changed from: package-private */
    public final boolean B0() {
        return T2.ORDERED.d(this.f);
    }

    public /* synthetic */ Spliterator C0() {
        return H0(0);
    }

    /* access modifiers changed from: package-private */
    public R1 D0(T1 t1, Spliterator spliterator, x xVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    /* access modifiers changed from: package-private */
    public Spliterator E0(T1 t1, Spliterator spliterator) {
        return D0(t1, spliterator, CLASSNAMEj.a).spliterator();
    }

    /* access modifiers changed from: package-private */
    public abstract boolean F0();

    /* access modifiers changed from: package-private */
    public abstract A2 G0(int i2, A2 a2);

    /* access modifiers changed from: package-private */
    public final Spliterator I0() {
        CLASSNAMEh1 h1Var = this.a;
        if (this != h1Var) {
            throw new IllegalStateException();
        } else if (!this.h) {
            this.h = true;
            Spliterator spliterator = h1Var.g;
            if (spliterator != null) {
                h1Var.g = null;
                return spliterator;
            }
            throw new IllegalStateException("source already consumed or closed");
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    /* access modifiers changed from: package-private */
    public abstract Spliterator J0(T1 t1, J j2, boolean z);

    public void close() {
        this.h = true;
        this.g = null;
        CLASSNAMEh1 h1Var = this.a;
        Runnable runnable = h1Var.j;
        if (runnable != null) {
            h1Var.j = null;
            runnable.run();
        }
    }

    public final boolean isParallel() {
        return this.a.k;
    }

    /* access modifiers changed from: package-private */
    public final void m0(A2 a2, Spliterator spliterator) {
        a2.getClass();
        if (!T2.SHORT_CIRCUIT.d(this.f)) {
            a2.m(spliterator.getExactSizeIfKnown());
            spliterator.forEachRemaining(a2);
            a2.l();
            return;
        }
        n0(a2, spliterator);
    }

    /* access modifiers changed from: package-private */
    public final void n0(A2 a2, Spliterator spliterator) {
        CLASSNAMEh1 h1Var = this;
        while (h1Var.e > 0) {
            h1Var = h1Var.b;
        }
        a2.m(spliterator.getExactSizeIfKnown());
        h1Var.z0(spliterator, a2);
        a2.l();
    }

    /* access modifiers changed from: package-private */
    public final R1 o0(Spliterator spliterator, boolean z, x xVar) {
        if (this.a.k) {
            return y0(this, spliterator, z, xVar);
        }
        R1.a s0 = s0(p0(spliterator), xVar);
        s0.getClass();
        m0(u0(s0), spliterator);
        return s0.a();
    }

    public CLASSNAMEl1 onClose(Runnable runnable) {
        CLASSNAMEh1 h1Var = this.a;
        Runnable runnable2 = h1Var.j;
        if (runnable2 != null) {
            runnable = new f3(runnable2, runnable);
        }
        h1Var.j = runnable;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final long p0(Spliterator spliterator) {
        if (T2.SIZED.d(this.f)) {
            return spliterator.getExactSizeIfKnown();
        }
        return -1;
    }

    public final CLASSNAMEl1 parallel() {
        this.a.k = true;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final U2 q0() {
        CLASSNAMEh1 h1Var = this;
        while (h1Var.e > 0) {
            h1Var = h1Var.b;
        }
        return h1Var.A0();
    }

    /* access modifiers changed from: package-private */
    public final int r0() {
        return this.f;
    }

    public final CLASSNAMEl1 sequential() {
        this.a.k = false;
        return this;
    }

    public Spliterator spliterator() {
        if (!this.h) {
            this.h = true;
            CLASSNAMEh1 h1Var = this.a;
            if (this != h1Var) {
                return J0(this, new CLASSNAMEk(this), h1Var.k);
            }
            Spliterator spliterator = h1Var.g;
            if (spliterator != null) {
                h1Var.g = null;
                return spliterator;
            }
            throw new IllegalStateException("source already consumed or closed");
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final A2 t0(A2 a2, Spliterator spliterator) {
        a2.getClass();
        m0(u0(a2), spliterator);
        return a2;
    }

    /* access modifiers changed from: package-private */
    public final A2 u0(A2 a2) {
        a2.getClass();
        for (CLASSNAMEh1 h1Var = this; h1Var.e > 0; h1Var = h1Var.b) {
            a2 = h1Var.G0(h1Var.b.f, a2);
        }
        return a2;
    }

    /* access modifiers changed from: package-private */
    public final Spliterator v0(Spliterator spliterator) {
        if (this.e == 0) {
            return spliterator;
        }
        return J0(this, new CLASSNAMEl(spliterator), this.a.k);
    }

    /* access modifiers changed from: package-private */
    public final Object w0(g3 g3Var) {
        if (!this.h) {
            this.h = true;
            return this.a.k ? g3Var.c(this, H0(g3Var.b())) : g3Var.d(this, H0(g3Var.b()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final R1 x0(x xVar) {
        if (!this.h) {
            this.h = true;
            if (!this.a.k || this.b == null || !F0()) {
                return o0(H0(0), true, xVar);
            }
            this.e = 0;
            CLASSNAMEh1 h1Var = this.b;
            return D0(h1Var, h1Var.H0(0), xVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract R1 y0(T1 t1, Spliterator spliterator, boolean z, x xVar);

    /* access modifiers changed from: package-private */
    public abstract void z0(Spliterator spliterator, A2 a2);
}
