package j$.util.stream;

import j$.util.function.e;
import j$.util.function.f;

public final /* synthetic */ class F implements f {
    public final /* synthetic */ int a = 1;
    public final /* synthetic */ Object b;

    public /* synthetic */ F(J j) {
        this.b = j;
    }

    public final void accept(double d) {
        switch (this.a) {
            case 0:
                ((CLASSNAMEn3) this.b).accept(d);
                return;
            default:
                ((J) this.b).a.accept(d);
                return;
        }
    }

    public f j(f fVar) {
        switch (this.a) {
            case 0:
                fVar.getClass();
                return new e(this, fVar);
            default:
                fVar.getClass();
                return new e(this, fVar);
        }
    }

    public /* synthetic */ F(CLASSNAMEn3 n3Var) {
        this.b = n3Var;
    }
}
