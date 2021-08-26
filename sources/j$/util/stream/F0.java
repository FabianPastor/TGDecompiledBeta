package j$.util.stream;

import j$.lang.a;
import j$.lang.b;
import j$.lang.c;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.function.m;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;

class F0 extends CLASSNAMEh3 {
    public final /* synthetic */ int b = 0;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(K k, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = k;
    }

    public void accept(int i) {
        switch (this.b) {
            case 0:
                this.a.accept((long) i);
                return;
            case 1:
                ((k) ((M) this.c).m).accept(i);
                this.a.accept(i);
                return;
            case 2:
                this.a.accept((double) i);
                return;
            case 3:
                this.a.accept(((CLASSNAMEb0) ((M) this.c).m).a(i));
                return;
            case 4:
                this.a.accept(((l) ((L) this.c).m).apply(i));
                return;
            case 5:
                this.a.accept(((m) ((N) this.c).m).applyAsLong(i));
                return;
            case 6:
                this.a.accept(((X) ((K) this.c).m).a(i));
                return;
            case 7:
                M0 m0 = (M0) ((l) ((M) this.c).m).apply(i);
                if (m0 != null) {
                    try {
                        m0.sequential().U(new B0(this));
                    } catch (Throwable unused) {
                        break;
                    }
                }
                if (m0 != null) {
                    m0.close();
                    return;
                }
                return;
            default:
                if (((V) ((M) this.c).m).b(i)) {
                    this.a.accept(i);
                    return;
                }
                return;
        }
        throw th;
    }

    public void n(long j) {
        switch (this.b) {
            case 7:
                this.a.n(-1);
                return;
            case 8:
                this.a.n(-1);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(L l, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = l;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEn3 n3Var, a aVar) {
        super(n3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEn3 n3Var, b bVar) {
        super(n3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEn3 n3Var, c cVar) {
        super(n3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(N n, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(O o, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = o;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(G0 g0, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = g0;
    }
}
