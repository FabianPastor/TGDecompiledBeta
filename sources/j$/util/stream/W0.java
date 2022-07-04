package j$.util.stream;

import j$.util.function.p;
import j$.util.function.q;

public final /* synthetic */ class W0 implements q {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;

    public /* synthetic */ W0(Z0 z0) {
        this.b = z0;
    }

    public final void accept(long j) {
        switch (this.a) {
            case 0:
                ((CLASSNAMEm3) this.b).accept(j);
                return;
            default:
                ((Z0) this.b).a.accept(j);
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

    public /* synthetic */ W0(CLASSNAMEm3 m3Var) {
        this.b = m3Var;
    }
}
