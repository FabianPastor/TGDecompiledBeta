package j$.util.stream;

import j$.util.function.y;
import j$.util.u;

/* renamed from: j$.util.stream.l1  reason: case insensitive filesystem */
final class CLASSNAMEl1 implements N4 {
    private final CLASSNAMEe4 a;
    final CLASSNAMEk1 b;
    final y c;

    CLASSNAMEl1(CLASSNAMEe4 e4Var, CLASSNAMEk1 k1Var, y yVar) {
        this.a = e4Var;
        this.b = k1Var;
        this.c = yVar;
    }

    public int b() {
        return CLASSNAMEd4.u | CLASSNAMEd4.r;
    }

    public Object c(CLASSNAMEy2 y2Var, u uVar) {
        return (Boolean) new CLASSNAMEm1(this, y2Var, uVar).invoke();
    }

    public Object d(CLASSNAMEy2 y2Var, u uVar) {
        CLASSNAMEj1 j1Var = (CLASSNAMEj1) this.c.get();
        CLASSNAMEc cVar = (CLASSNAMEc) y2Var;
        j1Var.getClass();
        cVar.n0(cVar.v0(j1Var), uVar);
        return Boolean.valueOf(j1Var.b);
    }
}
