package j$.util.stream;

import j$.lang.a;
import j$.lang.b;
import j$.lang.c;
import j$.util.function.f;
import j$.util.function.g;
import j$.util.function.h;
import j$.wrappers.E;
import j$.wrappers.G;
import j$.wrappers.K;

class J extends CLASSNAMEf3 {
    public final /* synthetic */ int b = 0;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(K k, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = k;
    }

    public void accept(double d) {
        switch (this.b) {
            case 0:
                this.a.accept(((K) ((K) this.c).m).a(d));
                return;
            case 1:
                this.a.accept(((g) ((L) this.c).m).apply(d));
                return;
            case 2:
                this.a.accept(((G) ((M) this.c).m).a(d));
                return;
            case 3:
                this.a.accept(((h) ((N) this.c).m).applyAsLong(d));
                return;
            case 4:
                U u = (U) ((g) ((K) this.c).m).apply(d);
                if (u != null) {
                    try {
                        u.sequential().j(new F(this));
                    } catch (Throwable unused) {
                        break;
                    }
                }
                if (u != null) {
                    u.close();
                    return;
                }
                return;
            case 5:
                if (((E) ((K) this.c).m).b(d)) {
                    this.a.accept(d);
                    return;
                }
                return;
            default:
                ((f) ((K) this.c).m).accept(d);
                this.a.accept(d);
                return;
        }
        throw th;
    }

    public void n(long j) {
        switch (this.b) {
            case 4:
                this.a.n(-1);
                return;
            case 5:
                this.a.n(-1);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(K k, CLASSNAMEm3 m3Var, a aVar) {
        super(m3Var);
        this.c = k;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(K k, CLASSNAMEm3 m3Var, b bVar) {
        super(m3Var);
        this.c = k;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(K k, CLASSNAMEm3 m3Var, c cVar) {
        super(m3Var);
        this.c = k;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(L l, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = l;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(M m, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public J(N n, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = n;
    }
}
