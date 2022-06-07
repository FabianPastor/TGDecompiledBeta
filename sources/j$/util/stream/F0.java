package j$.util.stream;

import j$.lang.a;
import j$.lang.b;
import j$.lang.c;
import j$.util.function.l;
import j$.util.function.m;
import j$.util.function.n;
import j$.wrappers.CLASSNAMEb0;
import j$.wrappers.V;
import j$.wrappers.X;

class F0 extends CLASSNAMEg3 {
    public final /* synthetic */ int b = 0;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(K k, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = k;
    }

    public void accept(int i) {
        switch (this.b) {
            case 0:
                this.a.accept((long) i);
                return;
            case 1:
                ((l) ((M) this.c).m).accept(i);
                this.a.accept(i);
                return;
            case 2:
                this.a.accept((double) i);
                return;
            case 3:
                this.a.accept(((CLASSNAMEb0) ((M) this.c).m).a(i));
                return;
            case 4:
                this.a.accept(((m) ((L) this.c).m).apply(i));
                return;
            case 5:
                this.a.accept(((n) ((N) this.c).m).applyAsLong(i));
                return;
            case 6:
                this.a.accept(((X) ((K) this.c).m).a(i));
                return;
            case 7:
                IntStream intStream = (IntStream) ((m) ((M) this.c).m).apply(i);
                if (intStream != null) {
                    try {
                        intStream.sequential().U(new B0(this));
                    } catch (Throwable unused) {
                        break;
                    }
                }
                if (intStream != null) {
                    intStream.close();
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
    public F0(L l, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = l;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEm3 m3Var, a aVar) {
        super(m3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEm3 m3Var, b bVar) {
        super(m3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(M m, CLASSNAMEm3 m3Var, c cVar) {
        super(m3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(N n, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(O o, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = o;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public F0(G0 g0, CLASSNAMEm3 m3Var) {
        super(m3Var);
        this.c = g0;
    }
}
