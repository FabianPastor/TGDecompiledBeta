package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.E;
import j$.util.function.v;

/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
abstract class CLASSNAMEh1 extends CLASSNAMEi4 implements CLASSNAMEl1 {
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
        int i3 = CLASSNAMEg6.l & i2;
        this.c = i3;
        this.f = ((i3 << 1) ^ -1) & CLASSNAMEg6.q;
        this.e = 0;
        this.k = z;
    }

    CLASSNAMEh1(CLASSNAMEh1 h1Var, int i2) {
        if (!h1Var.h) {
            h1Var.h = true;
            h1Var.d = this;
            this.b = h1Var;
            this.c = CLASSNAMEg6.m & i2;
            this.f = CLASSNAMEg6.a(i2, h1Var.f);
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
                        if (CLASSNAMEg6.SHORT_CIRCUIT.d(i6)) {
                            i6 &= CLASSNAMEg6.z ^ -1;
                        }
                        spliterator = h1Var2.E0(h1Var, spliterator);
                        if (spliterator.hasCharacteristics(64)) {
                            i4 = i6 & (CLASSNAMEg6.y ^ -1);
                            i3 = CLASSNAMEg6.x;
                        } else {
                            i4 = i6 & (CLASSNAMEg6.x ^ -1);
                            i3 = CLASSNAMEg6.y;
                        }
                        i6 = i4 | i3;
                    }
                    h1Var2.e = i5;
                    h1Var2.f = CLASSNAMEg6.a(i6, h1Var.f);
                    i5++;
                    CLASSNAMEh1 h1Var3 = h1Var2;
                    h1Var2 = h1Var2.d;
                    h1Var = h1Var3;
                }
            }
            if (i2 != 0) {
                this.f = CLASSNAMEg6.a(i2, this.f);
            }
            return spliterator;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEh6 A0();

    /* access modifiers changed from: package-private */
    public final boolean B0() {
        return CLASSNAMEg6.ORDERED.d(this.f);
    }

    public /* synthetic */ Spliterator C0() {
        return H0(0);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEl3 D0(CLASSNAMEi4 i4Var, Spliterator spliterator, v vVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    /* access modifiers changed from: package-private */
    public Spliterator E0(CLASSNAMEi4 i4Var, Spliterator spliterator) {
        return D0(i4Var, spliterator, CLASSNAMEj.a).spliterator();
    }

    /* access modifiers changed from: package-private */
    public abstract boolean F0();

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEt5 G0(int i2, CLASSNAMEt5 t5Var);

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
    public abstract Spliterator J0(CLASSNAMEi4 i4Var, E e2, boolean z);

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
    public final void m0(CLASSNAMEt5 t5Var, Spliterator spliterator) {
        t5Var.getClass();
        if (!CLASSNAMEg6.SHORT_CIRCUIT.d(this.f)) {
            t5Var.n(spliterator.getExactSizeIfKnown());
            spliterator.forEachRemaining(t5Var);
            t5Var.m();
            return;
        }
        n0(t5Var, spliterator);
    }

    /* access modifiers changed from: package-private */
    public final void n0(CLASSNAMEt5 t5Var, Spliterator spliterator) {
        CLASSNAMEh1 h1Var = this;
        while (h1Var.e > 0) {
            h1Var = h1Var.b;
        }
        t5Var.n(spliterator.getExactSizeIfKnown());
        h1Var.z0(spliterator, t5Var);
        t5Var.m();
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEl3 o0(Spliterator spliterator, boolean z, v vVar) {
        if (this.a.k) {
            return y0(this, spliterator, z, vVar);
        }
        CLASSNAMEg3 s0 = s0(p0(spliterator), vVar);
        s0.getClass();
        m0(u0(s0), spliterator);
        return s0.a();
    }

    public CLASSNAMEl1 onClose(Runnable runnable) {
        CLASSNAMEh1 h1Var = this.a;
        Runnable runnable2 = h1Var.j;
        if (runnable2 != null) {
            runnable = new I6(runnable2, runnable);
        }
        h1Var.j = runnable;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final long p0(Spliterator spliterator) {
        if (CLASSNAMEg6.SIZED.d(this.f)) {
            return spliterator.getExactSizeIfKnown();
        }
        return -1;
    }

    public final CLASSNAMEl1 parallel() {
        this.a.k = true;
        return this;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEh6 q0() {
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
    public final CLASSNAMEt5 t0(CLASSNAMEt5 t5Var, Spliterator spliterator) {
        t5Var.getClass();
        m0(u0(t5Var), spliterator);
        return t5Var;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt5 u0(CLASSNAMEt5 t5Var) {
        t5Var.getClass();
        for (CLASSNAMEh1 h1Var = this; h1Var.e > 0; h1Var = h1Var.b) {
            t5Var = h1Var.G0(h1Var.b.f, t5Var);
        }
        return t5Var;
    }

    /* access modifiers changed from: package-private */
    public final Spliterator v0(Spliterator spliterator) {
        if (this.e == 0) {
            return spliterator;
        }
        return J0(this, new CLASSNAMEl(spliterator), this.a.k);
    }

    /* access modifiers changed from: package-private */
    public final Object w0(J6 j6) {
        if (!this.h) {
            this.h = true;
            return this.a.k ? j6.c(this, H0(j6.b())) : j6.d(this, H0(j6.b()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEl3 x0(v vVar) {
        if (!this.h) {
            this.h = true;
            if (!this.a.k || this.b == null || !F0()) {
                return o0(H0(0), true, vVar);
            }
            this.e = 0;
            CLASSNAMEh1 h1Var = this.b;
            return D0(h1Var, h1Var.H0(0), vVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEl3 y0(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z, v vVar);

    /* access modifiers changed from: package-private */
    public abstract void z0(Spliterator spliterator, CLASSNAMEt5 t5Var);
}
