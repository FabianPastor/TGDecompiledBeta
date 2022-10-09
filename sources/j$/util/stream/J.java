package j$.util.stream;
/* loaded from: classes2.dex */
class J extends AbstractCLASSNAMEf3 {
    public final /* synthetic */ int b = 0;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEj3, j$.util.stream.InterfaceCLASSNAMEm3
    public void accept(double d) {
        switch (this.b) {
            case 0:
                this.a.accept(((j$.wrappers.K) ((K) this.c).m).a(d));
                return;
            case 1:
                this.a.accept((InterfaceCLASSNAMEm3) ((j$.util.function.g) ((L) this.c).m).apply(d));
                return;
            case 2:
                this.a.accept(((j$.wrappers.G) ((M) this.c).m).a(d));
                return;
            case 3:
                this.a.accept(((j$.util.function.h) ((N) this.c).m).applyAsLong(d));
                return;
            case 4:
                U u = (U) ((j$.util.function.g) ((K) this.c).m).apply(d);
                if (u != null) {
                    try {
                        u.mo305sequential().j(new F(this));
                    } finally {
                        try {
                            u.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (u == null) {
                    return;
                }
                return;
            case 5:
                if (!((j$.wrappers.E) ((K) this.c).m).b(d)) {
                    return;
                }
                this.a.accept(d);
                return;
            default:
                ((j$.util.function.f) ((K) this.c).m).accept(d);
                this.a.accept(d);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        switch (this.b) {
            case 4:
                this.a.n(-1L);
                return;
            case 5:
                this.a.n(-1L);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.a aVar) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.b bVar) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.c cVar) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(L l, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public J(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }
}
