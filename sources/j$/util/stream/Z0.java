package j$.util.stream;

import j$.lang.a;
import j$.lang.b;
import j$.lang.c;
import j$.util.function.q;
import j$.util.function.r;
import j$.util.function.t;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.CLASSNAMEl0;
import j$.wrappers.CLASSNAMEn0;

class Z0 extends CLASSNAMEh3 {
    public final /* synthetic */ int b = 4;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(K k, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = k;
    }

    public void accept(long j) {
        switch (this.b) {
            case 0:
                this.a.accept((double) j);
                return;
            case 1:
                this.a.accept(((t) ((N) this.c).m).applyAsLong(j));
                return;
            case 2:
                this.a.accept(((r) ((L) this.c).m).apply(j));
                return;
            case 3:
                this.a.accept(((CLASSNAMEn0) ((M) this.c).m).a(j));
                return;
            case 4:
                this.a.accept(((CLASSNAMEl0) ((K) this.c).m).a(j));
                return;
            case 5:
                CLASSNAMEe1 e1Var = (CLASSNAMEe1) ((r) ((N) this.c).m).apply(j);
                if (e1Var != null) {
                    try {
                        e1Var.sequential().d(new W0(this));
                    } catch (Throwable unused) {
                        break;
                    }
                }
                if (e1Var != null) {
                    e1Var.close();
                    return;
                }
                return;
            case 6:
                if (((CLASSNAMEj0) ((N) this.c).m).b(j)) {
                    this.a.accept(j);
                    return;
                }
                return;
            default:
                ((q) ((N) this.c).m).accept(j);
                this.a.accept(j);
                return;
        }
        throw th;
    }

    public void n(long j) {
        switch (this.b) {
            case 5:
                this.a.n(-1);
                return;
            case 6:
                this.a.n(-1);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(L l, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = l;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(M m, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(N n, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(N n, CLASSNAMEm3 m3Var, a aVar) {
        super(m3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(N n, CLASSNAMEm3 m3Var, b bVar) {
        super(m3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(N n, CLASSNAMEm3 m3Var, c cVar) {
        super(m3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z0(O o, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = o;
    }
}
