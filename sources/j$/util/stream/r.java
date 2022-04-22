package j$.util.stream;

import j$.util.function.Function;
import j$.util.function.f;
import j$.util.function.l;
import j$.util.function.q;
import java.util.HashSet;
import java.util.Set;

class r extends CLASSNAMEj3 {
    public final /* synthetic */ int b = 3;
    Object c;
    final /* synthetic */ Object d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public r(CLASSNAMEs sVar, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = sVar;
    }

    public void accept(Object obj) {
        switch (this.b) {
            case 0:
                if (!((Set) this.c).contains(obj)) {
                    ((Set) this.c).add(obj);
                    this.a.accept(obj);
                    return;
                }
                return;
            case 1:
                CLASSNAMEf1 f1Var = (CLASSNAMEf1) ((Function) ((N) this.d).m).apply(obj);
                if (f1Var != null) {
                    try {
                        f1Var.sequential().d((q) this.c);
                    } catch (Throwable unused) {
                        break;
                    }
                }
                if (f1Var != null) {
                    f1Var.close();
                    return;
                }
                return;
            case 2:
                M0 m0 = (M0) ((Function) ((M) this.d).m).apply(obj);
                if (m0 != null) {
                    try {
                        m0.sequential().U((l) this.c);
                    } catch (Throwable unused2) {
                        break;
                    }
                }
                if (m0 != null) {
                    m0.close();
                    return;
                }
                return;
            default:
                U u = (U) ((Function) ((K) this.d).m).apply(obj);
                if (u != null) {
                    try {
                        u.sequential().j((f) this.c);
                    } catch (Throwable unused3) {
                        break;
                    }
                }
                if (u != null) {
                    u.close();
                    return;
                }
                return;
        }
        throw th;
        throw th;
        throw th;
    }

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

    public void n(long j) {
        switch (this.b) {
            case 0:
                this.c = new HashSet();
                this.a.n(-1);
                return;
            case 1:
                this.a.n(-1);
                return;
            case 2:
                this.a.n(-1);
                return;
            default:
                this.a.n(-1);
                return;
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public r(K k, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = k;
        this.c = new F(n3Var);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public r(M m, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = m;
        this.c = new B0(n3Var);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public r(N n, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.d = n;
        this.c = new X0(n3Var);
    }
}
