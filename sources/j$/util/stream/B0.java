package j$.util.stream;

import j$.util.function.j;
import j$.util.function.k;

public final /* synthetic */ class B0 implements k {
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

    public k l(k kVar) {
        switch (this.a) {
            case 0:
                kVar.getClass();
                return new j(this, kVar);
            default:
                kVar.getClass();
                return new j(this, kVar);
        }
    }

    public /* synthetic */ B0(CLASSNAMEn3 n3Var) {
        this.b = n3Var;
    }
}
