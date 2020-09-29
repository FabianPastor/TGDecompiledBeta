package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.V;
import java.util.stream.Sink;

/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
abstract class CLASSNAMEh1 extends CLASSNAMEq4 implements CLASSNAMEl1 {
    private final CLASSNAMEh1 a;
    private final CLASSNAMEh1 b;
    protected final int c;
    private CLASSNAMEh1 d;
    private int e;
    private int f;
    private Spliterator g;
    private V h;
    private boolean i;
    private boolean j;
    private Runnable k;
    private boolean l;

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEv6 A0();

    /* access modifiers changed from: package-private */
    public abstract Spliterator F0(V v);

    /* access modifiers changed from: package-private */
    public abstract boolean I0();

    /* access modifiers changed from: package-private */
    public abstract G5 J0(int i2, G5 g5);

    /* access modifiers changed from: package-private */
    public abstract Spliterator M0(CLASSNAMEq4 q4Var, V v, boolean z);

    /* access modifiers changed from: package-private */
    public abstract CLASSNAMEt3 y0(CLASSNAMEq4 q4Var, Spliterator spliterator, boolean z, C c2);

    /* access modifiers changed from: package-private */
    public abstract void z0(Spliterator spliterator, G5 g5);

    static {
        Class<CLASSNAMEh1> cls = CLASSNAMEh1.class;
    }

    CLASSNAMEh1(Spliterator spliterator, int sourceFlags, boolean parallel) {
        this.b = null;
        this.g = spliterator;
        this.a = this;
        int i2 = CLASSNAMEu6.l & sourceFlags;
        this.c = i2;
        this.f = ((i2 << 1) ^ -1) & CLASSNAMEu6.q;
        this.e = 0;
        this.l = parallel;
    }

    CLASSNAMEh1(CLASSNAMEh1 previousStage, int opFlags) {
        if (!previousStage.i) {
            previousStage.i = true;
            previousStage.d = this;
            this.b = previousStage;
            this.c = CLASSNAMEu6.m & opFlags;
            this.f = CLASSNAMEu6.i(opFlags, previousStage.f);
            this.a = previousStage.a;
            if (I0()) {
                this.a.j = true;
            }
            this.e = previousStage.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final Object w0(f7 terminalOp) {
        if (!this.i) {
            this.i = true;
            if (isParallel()) {
                return terminalOp.c(this, K0(terminalOp.a()));
            }
            return terminalOp.d(this, K0(terminalOp.a()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt3 x0(C c2) {
        if (!this.i) {
            this.i = true;
            if (!isParallel() || this.b == null || !I0()) {
                return e(K0(0), true, c2);
            }
            this.e = 0;
            CLASSNAMEh1 h1Var = this.b;
            return G0(h1Var, h1Var.K0(0), c2);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final Spliterator L0() {
        CLASSNAMEh1 h1Var = this.a;
        if (this != h1Var) {
            throw new IllegalStateException();
        } else if (!this.i) {
            this.i = true;
            if (h1Var.g != null) {
                Spliterator spliterator = h1Var.g;
                h1Var.g = null;
                return spliterator;
            }
            V v = h1Var.h;
            if (v != null) {
                Spliterator spliterator2 = (Spliterator) v.get();
                this.a.h = null;
                return spliterator2;
            }
            throw new IllegalStateException("source already consumed or closed");
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    public final CLASSNAMEl1 sequential() {
        this.a.l = false;
        return this;
    }

    public final CLASSNAMEl1 parallel() {
        this.a.l = true;
        return this;
    }

    public void close() {
        this.i = true;
        this.h = null;
        this.g = null;
        CLASSNAMEh1 h1Var = this.a;
        if (h1Var.k != null) {
            Runnable closeAction = h1Var.k;
            h1Var.k = null;
            closeAction.run();
        }
    }

    public CLASSNAMEl1 onClose(Runnable closeHandler) {
        Runnable runnable;
        CLASSNAMEh1 h1Var = this.a;
        Runnable existingHandler = h1Var.k;
        if (existingHandler == null) {
            runnable = closeHandler;
        } else {
            runnable = d7.a(existingHandler, closeHandler);
        }
        h1Var.k = runnable;
        return this;
    }

    public Spliterator spliterator() {
        if (!this.i) {
            this.i = true;
            CLASSNAMEh1 h1Var = this.a;
            if (this != h1Var) {
                return M0(this, new CLASSNAMEh(this), isParallel());
            }
            if (h1Var.g != null) {
                Spliterator spliterator = h1Var.g;
                h1Var.g = null;
                return spliterator;
            } else if (h1Var.h != null) {
                V v = h1Var.h;
                h1Var.h = null;
                return F0(v);
            } else {
                throw new IllegalStateException("source already consumed or closed");
            }
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    public /* synthetic */ Spliterator D0() {
        return K0(0);
    }

    public final boolean isParallel() {
        return this.a.l;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: j$.util.Spliterator} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private j$.util.Spliterator K0(int r9) {
        /*
            r8 = this;
            r0 = 0
            j$.util.stream.h1 r1 = r8.a
            j$.util.Spliterator r2 = r1.g
            r3 = 0
            if (r2 == 0) goto L_0x000d
            j$.util.Spliterator r0 = r1.g
            r1.g = r3
            goto L_0x001c
        L_0x000d:
            j$.util.function.V r1 = r1.h
            if (r1 == 0) goto L_0x0080
            java.lang.Object r1 = r1.get()
            r0 = r1
            j$.util.Spliterator r0 = (j$.util.Spliterator) r0
            j$.util.stream.h1 r1 = r8.a
            r1.h = r3
        L_0x001c:
            boolean r1 = r8.isParallel()
            if (r1 == 0) goto L_0x0075
            j$.util.stream.h1 r1 = r8.a
            boolean r2 = r1.j
            if (r2 == 0) goto L_0x0075
            r2 = 1
            j$.util.stream.h1 r3 = r8.a
            j$.util.stream.h1 r1 = r1.d
            r4 = r8
        L_0x002e:
            if (r3 == r4) goto L_0x0075
            int r5 = r1.c
            boolean r6 = r1.I0()
            if (r6 == 0) goto L_0x0064
            r2 = 0
            j$.util.stream.u6 r6 = j$.util.stream.CLASSNAMEu6.SHORT_CIRCUIT
            boolean r6 = r6.K(r5)
            if (r6 == 0) goto L_0x0046
            int r6 = j$.util.stream.CLASSNAMEu6.z
            r6 = r6 ^ -1
            r5 = r5 & r6
        L_0x0046:
            j$.util.Spliterator r0 = r1.H0(r3, r0)
            r6 = 64
            boolean r6 = r0.hasCharacteristics(r6)
            if (r6 == 0) goto L_0x005b
            int r6 = j$.util.stream.CLASSNAMEu6.y
            r6 = r6 ^ -1
            r6 = r6 & r5
            int r7 = j$.util.stream.CLASSNAMEu6.x
            r6 = r6 | r7
            goto L_0x0063
        L_0x005b:
            int r6 = j$.util.stream.CLASSNAMEu6.x
            r6 = r6 ^ -1
            r6 = r6 & r5
            int r7 = j$.util.stream.CLASSNAMEu6.y
            r6 = r6 | r7
        L_0x0063:
            r5 = r6
        L_0x0064:
            int r6 = r2 + 1
            r1.e = r2
            int r2 = r3.f
            int r2 = j$.util.stream.CLASSNAMEu6.i(r5, r2)
            r1.f = r2
            r3 = r1
            j$.util.stream.h1 r1 = r1.d
            r2 = r6
            goto L_0x002e
        L_0x0075:
            if (r9 == 0) goto L_0x007f
            int r1 = r8.f
            int r1 = j$.util.stream.CLASSNAMEu6.i(r9, r1)
            r8.f = r1
        L_0x007f:
            return r0
        L_0x0080:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "source already consumed or closed"
            r1.<init>(r2)
            goto L_0x0089
        L_0x0088:
            throw r1
        L_0x0089:
            goto L_0x0088
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEh1.K0(int):j$.util.Spliterator");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEv6 q0() {
        CLASSNAMEh1 p = this;
        while (p.e > 0) {
            p = p.b;
        }
        return p.A0();
    }

    /* access modifiers changed from: package-private */
    public final long p0(Spliterator spliterator) {
        if (CLASSNAMEu6.SIZED.K(r0())) {
            return spliterator.getExactSizeIfKnown();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final G5 t0(G5 sink, Spliterator spliterator) {
        sink.getClass();
        c(u0(sink), spliterator);
        return sink;
    }

    /* access modifiers changed from: package-private */
    public final void c(G5 wrappedSink, Spliterator spliterator) {
        wrappedSink.getClass();
        if (!CLASSNAMEu6.SHORT_CIRCUIT.K(r0())) {
            wrappedSink.s(spliterator.getExactSizeIfKnown());
            spliterator.forEachRemaining(wrappedSink);
            wrappedSink.r();
            return;
        }
        d(wrappedSink, spliterator);
    }

    /* access modifiers changed from: package-private */
    public final void d(G5 wrappedSink, Spliterator spliterator) {
        CLASSNAMEh1 p = this;
        while (p.e > 0) {
            p = p.b;
        }
        wrappedSink.s(spliterator.getExactSizeIfKnown());
        p.z0(spliterator, wrappedSink);
        wrappedSink.r();
    }

    /* access modifiers changed from: package-private */
    public final int r0() {
        return this.f;
    }

    /* access modifiers changed from: package-private */
    public final boolean B0() {
        return CLASSNAMEu6.ORDERED.K(this.f);
    }

    /* access modifiers changed from: package-private */
    public final G5 u0(Sink<E_OUT> sink) {
        sink.getClass();
        for (CLASSNAMEh1 p = this; p.e > 0; p = p.b) {
            sink = p.J0(p.b.f, sink);
        }
        return sink;
    }

    /* access modifiers changed from: package-private */
    public final Spliterator v0(Spliterator spliterator) {
        if (this.e == 0) {
            return spliterator;
        }
        return M0(this, new CLASSNAMEi(spliterator), isParallel());
    }

    static /* synthetic */ Spliterator E0(Spliterator sourceSpliterator) {
        return sourceSpliterator;
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEt3 e(Spliterator spliterator, boolean flatten, C c2) {
        if (isParallel()) {
            return y0(this, spliterator, flatten, c2);
        }
        CLASSNAMEk3 s0 = s0(p0(spliterator), c2);
        t0(s0, spliterator);
        return s0.b();
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt3 G0(CLASSNAMEq4 q4Var, Spliterator spliterator, C c2) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    static /* synthetic */ Object[] C0(int i2) {
        return new Object[i2];
    }

    /* access modifiers changed from: package-private */
    public Spliterator H0(CLASSNAMEq4 helper, Spliterator spliterator) {
        return G0(helper, spliterator, CLASSNAMEj.a).spliterator();
    }
}
