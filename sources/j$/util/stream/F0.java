package j$.util.stream;

import j$.wrappers.CLASSNAMEb0;
/* loaded from: classes2.dex */
class F0 extends AbstractCLASSNAMEg3 {
    public final /* synthetic */ int b = 0;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEk3, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(int i) {
        switch (this.b) {
            case 0:
                this.a.accept(i);
                return;
            case 1:
                ((j$.util.function.l) ((M) this.c).m).accept(i);
                this.a.accept(i);
                return;
            case 2:
                this.a.accept(i);
                return;
            case 3:
                this.a.accept(((CLASSNAMEb0) ((M) this.c).m).a(i));
                return;
            case 4:
                this.a.accept((InterfaceCLASSNAMEm3) ((j$.util.function.m) ((L) this.c).m).apply(i));
                return;
            case 5:
                this.a.accept(((j$.util.function.n) ((N) this.c).m).applyAsLong(i));
                return;
            case 6:
                this.a.accept(((j$.wrappers.X) ((K) this.c).m).a(i));
                return;
            case 7:
                IntStream intStream = (IntStream) ((j$.util.function.m) ((M) this.c).m).apply(i);
                if (intStream != null) {
                    try {
                        intStream.mo305sequential().U(new B0(this));
                    } finally {
                        try {
                            intStream.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (intStream == null) {
                    return;
                }
                return;
            default:
                if (!((j$.wrappers.V) ((M) this.c).m).b(i)) {
                    return;
                }
                this.a.accept(i);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        switch (this.b) {
            case 7:
                this.a.n(-1L);
                return;
            case 8:
                this.a.n(-1L);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(L l, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.a aVar) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.b bVar) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.c cVar) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(O o, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = o;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public F0(G0 g0, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = g0;
    }
}
