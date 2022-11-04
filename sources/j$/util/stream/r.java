package j$.util.stream;

import j$.util.function.Function;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes2.dex */
class r extends AbstractCLASSNAMEi3 {
    public final /* synthetic */ int b = 3;
    Object c;
    final /* synthetic */ Object d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(CLASSNAMEs CLASSNAMEs, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.d = CLASSNAMEs;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.b) {
            case 0:
                if (((Set) this.c).contains(obj)) {
                    return;
                }
                ((Set) this.c).add(obj);
                this.a.accept((InterfaceCLASSNAMEm3) obj);
                return;
            case 1:
                InterfaceCLASSNAMEe1 interfaceCLASSNAMEe1 = (InterfaceCLASSNAMEe1) ((Function) ((N) this.d).m).apply(obj);
                if (interfaceCLASSNAMEe1 != null) {
                    try {
                        interfaceCLASSNAMEe1.mo309sequential().d((j$.util.function.q) this.c);
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
            case 2:
                IntStream intStream = (IntStream) ((Function) ((M) this.d).m).apply(obj);
                if (intStream != null) {
                    try {
                        intStream.mo309sequential().U((j$.util.function.l) this.c);
                    } finally {
                        try {
                            intStream.close();
                        } catch (Throwable unused2) {
                        }
                    }
                }
                if (intStream == null) {
                    return;
                }
                return;
            default:
                U u = (U) ((Function) ((K) this.d).m).apply(obj);
                if (u != null) {
                    try {
                        u.mo309sequential().j((j$.util.function.f) this.c);
                    } finally {
                        try {
                            u.close();
                        } catch (Throwable unused3) {
                        }
                    }
                }
                if (u == null) {
                    return;
                }
                return;
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEi3, j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
        switch (this.b) {
            case 0:
                this.c = null;
                this.a.m();
                return;
            default:
                this.a.m();
                return;
        }
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        switch (this.b) {
            case 0:
                this.c = new HashSet();
                this.a.n(-1L);
                return;
            case 1:
                this.a.n(-1L);
                return;
            case 2:
                this.a.n(-1L);
                return;
            default:
                this.a.n(-1L);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(K k, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.d = k;
        this.c = new F(interfaceCLASSNAMEm3);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(M m, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.d = m;
        this.c = new B0(interfaceCLASSNAMEm3);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(N n, InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
        this.d = n;
        this.c = new W0(interfaceCLASSNAMEm3);
    }
}
