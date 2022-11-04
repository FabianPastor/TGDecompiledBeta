package j$.util.stream;
/* renamed from: j$.util.stream.c  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEc extends AbstractCLASSNAMEy2 implements InterfaceCLASSNAMEg {
    private final AbstractCLASSNAMEc a;
    private final AbstractCLASSNAMEc b;
    protected final int c;
    private AbstractCLASSNAMEc d;
    private int e;
    private int f;
    private j$.util.u g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEc(AbstractCLASSNAMEc abstractCLASSNAMEc, int i) {
        if (!abstractCLASSNAMEc.h) {
            abstractCLASSNAMEc.h = true;
            abstractCLASSNAMEc.d = this;
            this.b = abstractCLASSNAMEc;
            this.c = EnumCLASSNAMEd4.h & i;
            this.f = EnumCLASSNAMEd4.a(i, abstractCLASSNAMEc.f);
            AbstractCLASSNAMEc abstractCLASSNAMEc2 = abstractCLASSNAMEc.a;
            this.a = abstractCLASSNAMEc2;
            if (G0()) {
                abstractCLASSNAMEc2.i = true;
            }
            this.e = abstractCLASSNAMEc.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCLASSNAMEc(j$.util.u uVar, int i, boolean z) {
        this.b = null;
        this.g = uVar;
        this.a = this;
        int i2 = EnumCLASSNAMEd4.g & i;
        this.c = i2;
        this.f = ((i2 << 1) ^ (-1)) & EnumCLASSNAMEd4.l;
        this.e = 0;
        this.k = z;
    }

    private j$.util.u I0(int i) {
        int i2;
        int i3;
        AbstractCLASSNAMEc abstractCLASSNAMEc = this.a;
        j$.util.u uVar = abstractCLASSNAMEc.g;
        if (uVar != null) {
            abstractCLASSNAMEc.g = null;
            if (abstractCLASSNAMEc.k && abstractCLASSNAMEc.i) {
                AbstractCLASSNAMEc abstractCLASSNAMEc2 = abstractCLASSNAMEc.d;
                int i4 = 1;
                while (abstractCLASSNAMEc != this) {
                    int i5 = abstractCLASSNAMEc2.c;
                    if (abstractCLASSNAMEc2.G0()) {
                        i4 = 0;
                        if (EnumCLASSNAMEd4.SHORT_CIRCUIT.d(i5)) {
                            i5 &= EnumCLASSNAMEd4.u ^ (-1);
                        }
                        uVar = abstractCLASSNAMEc2.F0(abstractCLASSNAMEc, uVar);
                        if (uVar.hasCharacteristics(64)) {
                            i2 = i5 & (EnumCLASSNAMEd4.t ^ (-1));
                            i3 = EnumCLASSNAMEd4.s;
                        } else {
                            i2 = i5 & (EnumCLASSNAMEd4.s ^ (-1));
                            i3 = EnumCLASSNAMEd4.t;
                        }
                        i5 = i2 | i3;
                    }
                    abstractCLASSNAMEc2.e = i4;
                    abstractCLASSNAMEc2.f = EnumCLASSNAMEd4.a(i5, abstractCLASSNAMEc.f);
                    i4++;
                    AbstractCLASSNAMEc abstractCLASSNAMEc3 = abstractCLASSNAMEc2;
                    abstractCLASSNAMEc2 = abstractCLASSNAMEc2.d;
                    abstractCLASSNAMEc = abstractCLASSNAMEc3;
                }
            }
            if (i != 0) {
                this.f = EnumCLASSNAMEd4.a(i, this.f);
            }
            return uVar;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    abstract void A0(j$.util.u uVar, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract EnumCLASSNAMEe4 B0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean C0() {
        return EnumCLASSNAMEd4.ORDERED.d(this.f);
    }

    public /* synthetic */ j$.util.u D0() {
        return I0(0);
    }

    A1 E0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.m mVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    j$.util.u F0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        return E0(abstractCLASSNAMEy2, uVar, CLASSNAMEa.a).mo289spliterator();
    }

    abstract boolean G0();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract InterfaceCLASSNAMEm3 H0(int i, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final j$.util.u J0() {
        AbstractCLASSNAMEc abstractCLASSNAMEc = this.a;
        if (this == abstractCLASSNAMEc) {
            if (this.h) {
                throw new IllegalStateException("stream has already been operated upon or closed");
            }
            this.h = true;
            j$.util.u uVar = abstractCLASSNAMEc.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractCLASSNAMEc.g = null;
            return uVar;
        }
        throw new IllegalStateException();
    }

    abstract j$.util.u K0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.function.y yVar, boolean z);

    @Override // j$.util.stream.InterfaceCLASSNAMEg, java.lang.AutoCloseable
    public void close() {
        this.h = true;
        this.g = null;
        AbstractCLASSNAMEc abstractCLASSNAMEc = this.a;
        Runnable runnable = abstractCLASSNAMEc.j;
        if (runnable != null) {
            abstractCLASSNAMEc.j = null;
            runnable.run();
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public final boolean isParallel() {
        return this.a.k;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final void n0(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.util.u uVar) {
        interfaceCLASSNAMEm3.getClass();
        if (EnumCLASSNAMEd4.SHORT_CIRCUIT.d(this.f)) {
            o0(interfaceCLASSNAMEm3, uVar);
            return;
        }
        interfaceCLASSNAMEm3.n(uVar.getExactSizeIfKnown());
        uVar.forEachRemaining(interfaceCLASSNAMEm3);
        interfaceCLASSNAMEm3.m();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final void o0(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.util.u uVar) {
        AbstractCLASSNAMEc abstractCLASSNAMEc = this;
        while (abstractCLASSNAMEc.e > 0) {
            abstractCLASSNAMEc = abstractCLASSNAMEc.b;
        }
        interfaceCLASSNAMEm3.n(uVar.getExactSizeIfKnown());
        abstractCLASSNAMEc.A0(uVar, interfaceCLASSNAMEm3);
        interfaceCLASSNAMEm3.m();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEg
    public InterfaceCLASSNAMEg onClose(Runnable runnable) {
        AbstractCLASSNAMEc abstractCLASSNAMEc = this.a;
        Runnable runnable2 = abstractCLASSNAMEc.j;
        if (runnable2 != null) {
            runnable = new M4(runnable2, runnable);
        }
        abstractCLASSNAMEc.j = runnable;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final A1 p0(j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        if (this.a.k) {
            return z0(this, uVar, z, mVar);
        }
        InterfaceCLASSNAMEs1 t0 = t0(q0(uVar), mVar);
        t0.getClass();
        n0(v0(t0), uVar);
        return t0.mo291a();
    }

    /* renamed from: parallel */
    public final InterfaceCLASSNAMEg mo308parallel() {
        this.a.k = true;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final long q0(j$.util.u uVar) {
        if (EnumCLASSNAMEd4.SIZED.d(this.f)) {
            return uVar.getExactSizeIfKnown();
        }
        return -1L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final EnumCLASSNAMEe4 r0() {
        AbstractCLASSNAMEc abstractCLASSNAMEc = this;
        while (abstractCLASSNAMEc.e > 0) {
            abstractCLASSNAMEc = abstractCLASSNAMEc.b;
        }
        return abstractCLASSNAMEc.B0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final int s0() {
        return this.f;
    }

    /* renamed from: sequential */
    public final InterfaceCLASSNAMEg mo309sequential() {
        this.a.k = false;
        return this;
    }

    /* renamed from: spliterator */
    public j$.util.u mo310spliterator() {
        if (!this.h) {
            this.h = true;
            AbstractCLASSNAMEc abstractCLASSNAMEc = this.a;
            if (this != abstractCLASSNAMEc) {
                return K0(this, new CLASSNAMEb(this), abstractCLASSNAMEc.k);
            }
            j$.util.u uVar = abstractCLASSNAMEc.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractCLASSNAMEc.g = null;
            return uVar;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final InterfaceCLASSNAMEm3 u0(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.util.u uVar) {
        interfaceCLASSNAMEm3.getClass();
        n0(v0(interfaceCLASSNAMEm3), uVar);
        return interfaceCLASSNAMEm3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final InterfaceCLASSNAMEm3 v0(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        interfaceCLASSNAMEm3.getClass();
        for (AbstractCLASSNAMEc abstractCLASSNAMEc = this; abstractCLASSNAMEc.e > 0; abstractCLASSNAMEc = abstractCLASSNAMEc.b) {
            interfaceCLASSNAMEm3 = abstractCLASSNAMEc.H0(abstractCLASSNAMEc.b.f, interfaceCLASSNAMEm3);
        }
        return interfaceCLASSNAMEm3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractCLASSNAMEy2
    public final j$.util.u w0(j$.util.u uVar) {
        return this.e == 0 ? uVar : K0(this, new CLASSNAMEb(uVar), this.a.k);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object x0(N4 n4) {
        if (!this.h) {
            this.h = true;
            return this.a.k ? n4.c(this, I0(n4.b())) : n4.d(this, I0(n4.b()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final A1 y0(j$.util.function.m mVar) {
        if (!this.h) {
            this.h = true;
            if (!this.a.k || this.b == null || !G0()) {
                return p0(I0(0), true, mVar);
            }
            this.e = 0;
            AbstractCLASSNAMEc abstractCLASSNAMEc = this.b;
            return E0(abstractCLASSNAMEc, abstractCLASSNAMEc.I0(0), mVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    abstract A1 z0(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, boolean z, j$.util.function.m mVar);
}
