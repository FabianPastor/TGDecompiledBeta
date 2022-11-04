package j$.util.stream;

import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;
/* loaded from: classes2.dex */
class Z0 extends AbstractCLASSNAMEh3 {
    public final /* synthetic */ int b = 4;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = k;
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public void accept(long j) {
        switch (this.b) {
            case 0:
                this.a.accept(j);
                return;
            case 1:
                this.a.accept(((j$.util.function.t) ((N) this.c).m).applyAsLong(j));
                return;
            case 2:
                this.a.accept((InterfaceCLASSNAMEm3) ((j$.util.function.r) ((L) this.c).m).apply(j));
                return;
            case 3:
                this.a.accept(((CLASSNAMEn0) ((M) this.c).m).a(j));
                return;
            case 4:
                this.a.accept(((CLASSNAMEl0) ((K) this.c).m).a(j));
                return;
            case 5:
                InterfaceCLASSNAMEe1 interfaceCLASSNAMEe1 = (InterfaceCLASSNAMEe1) ((j$.util.function.r) ((N) this.c).m).apply(j);
                if (interfaceCLASSNAMEe1 != null) {
                    try {
                        interfaceCLASSNAMEe1.mo309sequential().d(new W0(this));
                    } finally {
                        try {
                            interfaceCLASSNAMEe1.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (interfaceCLASSNAMEe1 == null) {
                    return;
                }
                return;
            case 6:
                if (!((CLASSNAMEj0) ((N) this.c).m).b(j)) {
                    return;
                }
                this.a.accept(j);
                return;
            default:
                ((j$.util.function.q) ((N) this.c).m).accept(j);
                this.a.accept(j);
                return;
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        switch (this.b) {
            case 5:
                this.a.n(-1L);
                return;
            case 6:
                this.a.n(-1L);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(L l, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.a aVar) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.b bVar) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3, j$.lang.c cVar) {
        super(interfaceCLASSNAMEm3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(O o, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.c = o;
    }
}
