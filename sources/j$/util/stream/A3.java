package j$.util.stream;
/* loaded from: classes2.dex */
final class A3 extends AbstractCLASSNAMEd {
    private final AbstractCLASSNAMEc j;
    private final j$.util.function.m k;
    private final long l;
    private final long m;
    private long n;
    private volatile boolean o;

    A3(A3 a3, j$.util.u uVar) {
        super(a3, uVar);
        this.j = a3.j;
        this.k = a3.k;
        this.l = a3.l;
        this.m = a3.m;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public A3(AbstractCLASSNAMEc abstractCLASSNAMEc, AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar, j$.util.function.m mVar, long j, long j2) {
        super(abstractCLASSNAMEy2, uVar);
        this.j = abstractCLASSNAMEc;
        this.k = mVar;
        this.l = j;
        this.m = j2;
    }

    private long m(long j) {
        if (this.o) {
            return this.n;
        }
        A3 a3 = (A3) this.d;
        A3 a32 = (A3) this.e;
        if (a3 == null || a32 == null) {
            return this.n;
        }
        long m = a3.m(j);
        return m >= j ? m : m + a32.m(j);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public Object a() {
        long j = -1;
        if (e()) {
            if (EnumCLASSNAMEd4.SIZED.e(this.j.c)) {
                j = this.j.q0(this.b);
            }
            InterfaceCLASSNAMEs1 t0 = this.j.t0(j, this.k);
            InterfaceCLASSNAMEm3 H0 = this.j.H0(this.a.s0(), t0);
            AbstractCLASSNAMEy2 abstractCLASSNAMEy2 = this.a;
            abstractCLASSNAMEy2.o0(abstractCLASSNAMEy2.v0(H0), this.b);
            return t0.moNUMa();
        }
        AbstractCLASSNAMEy2 abstractCLASSNAMEy22 = this.a;
        InterfaceCLASSNAMEs1 t02 = abstractCLASSNAMEy22.t0(-1L, this.k);
        abstractCLASSNAMEy22.u0(t02, this.b);
        A1 moNUMa = t02.moNUMa();
        this.n = moNUMa.count();
        this.o = true;
        this.b = null;
        return moNUMa;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public AbstractCLASSNAMEf f(j$.util.u uVar) {
        return new A3(this, uVar);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEd
    protected void i() {
        this.i = true;
        if (this.o) {
            g(k());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEd
    /* renamed from: n */
    public final A1 k() {
        return AbstractCLASSNAMEx2.k(this.j.B0());
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0065  */
    @Override // j$.util.stream.AbstractCLASSNAMEf, java.util.concurrent.CountedCompleter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void onCompletion(java.util.concurrent.CountedCompleter r12) {
        /*
            Method dump skipped, instructions count: 228
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.A3.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
