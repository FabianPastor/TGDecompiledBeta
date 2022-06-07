package j$.util.stream;

import j$.util.u;

abstract class U2 implements N4 {
    private final CLASSNAMEe4 a;

    U2(CLASSNAMEe4 e4Var) {
        this.a = e4Var;
    }

    public abstract S2 a();

    public /* synthetic */ int b() {
        return 0;
    }

    public Object c(CLASSNAMEy2 y2Var, u uVar) {
        return ((S2) new V2(this, y2Var, uVar).invoke()).get();
    }

    public Object d(CLASSNAMEy2 y2Var, u uVar) {
        S2 a2 = a();
        CLASSNAMEc cVar = (CLASSNAMEc) y2Var;
        a2.getClass();
        cVar.n0(cVar.v0(a2), uVar);
        return a2.get();
    }
}
