package j$.util.stream;

import j$.util.function.k;
import j$.util.function.l;

public final /* synthetic */ class B0 implements l {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;

    public /* synthetic */ B0(F0 f0) {
        this.b = f0;
    }

    public final void accept(int i) {
        switch (this.a) {
            case 0:
                ((CLASSNAMEn3) this.b).accept(i);
                return;
            default:
                ((F0) this.b).a.accept(i);
                return;
        }
    }

    public l l(l lVar) {
        switch (this.a) {
            case 0:
                lVar.getClass();
                return new k(this, lVar);
            default:
                lVar.getClass();
                return new k(this, lVar);
        }
    }

    public /* synthetic */ B0(CLASSNAMEn3 n3Var) {
        this.b = n3Var;
    }
}
