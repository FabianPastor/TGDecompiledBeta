package j$.util.stream;

import j$.util.function.p;
import j$.util.function.q;

public final /* synthetic */ class X0 implements q {
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

    public q f(q qVar) {
        switch (this.a) {
            case 0:
                qVar.getClass();
                return new p(this, qVar);
            default:
                qVar.getClass();
                return new p(this, qVar);
        }
    }

    public /* synthetic */ X0(CLASSNAMEn3 n3Var) {
        this.b = n3Var;
    }
}
