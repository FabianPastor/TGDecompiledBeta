package j$.util.stream;

import j$.util.function.o;
import j$.util.function.p;

public final /* synthetic */ class X0 implements p {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;

    public /* synthetic */ X0(CLASSNAMEa1 a1Var) {
        this.b = a1Var;
    }

    public final void accept(long j) {
        switch (this.a) {
            case 0:
                ((CLASSNAMEn3) this.b).accept(j);
                return;
            default:
                ((CLASSNAMEa1) this.b).a.accept(j);
                return;
        }
    }

    public p f(p pVar) {
        switch (this.a) {
            case 0:
                pVar.getClass();
                return new o(this, pVar);
            default:
                pVar.getClass();
                return new o(this, pVar);
        }
    }

    public /* synthetic */ X0(CLASSNAMEn3 n3Var) {
        this.b = n3Var;
    }
}
