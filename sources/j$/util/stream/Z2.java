package j$.util.stream;

import j$.lang.a;
import j$.util.function.A;
import j$.util.function.B;
import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.function.y;

class Z2 extends CLASSNAMEj3 {
    public final /* synthetic */ int b = 5;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z2(K k, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = k;
    }

    public void accept(Object obj) {
        switch (this.b) {
            case 0:
                ((Consumer) ((L) this.c).m).accept(obj);
                this.a.accept(obj);
                return;
            case 1:
                if (((y) ((L) this.c).m).test(obj)) {
                    this.a.accept(obj);
                    return;
                }
                return;
            case 2:
                this.a.accept(((CLASSNAMEb3) this.c).m.apply(obj));
                return;
            case 3:
                this.a.accept(((B) ((M) this.c).m).applyAsInt(obj));
                return;
            case 4:
                this.a.accept(((C) ((N) this.c).m).applyAsLong(obj));
                return;
            case 5:
                this.a.accept(((A) ((K) this.c).m).applyAsDouble(obj));
                return;
            default:
                Stream stream = (Stream) ((CLASSNAMEb3) this.c).m.apply(obj);
                if (stream != null) {
                    try {
                        ((Stream) stream.sequential()).forEach(this.a);
                    } catch (Throwable unused) {
                        break;
                    }
                }
                if (stream != null) {
                    stream.close();
                    return;
                }
                return;
        }
        throw th;
    }

    public void n(long j) {
        switch (this.b) {
            case 1:
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
    public Z2(L l, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = l;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z2(L l, CLASSNAMEn3 n3Var, a aVar) {
        super(n3Var);
        this.c = l;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z2(M m, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = m;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z2(N n, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = n;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z2(CLASSNAMEb3 b3Var, CLASSNAMEn3 n3Var) {
        super(n3Var);
        this.c = b3Var;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Z2(CLASSNAMEb3 b3Var, CLASSNAMEn3 n3Var, a aVar) {
        super(n3Var);
        this.c = b3Var;
    }
}
