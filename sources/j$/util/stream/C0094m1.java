package j$.util.stream;

import j$.util.function.y;

/* renamed from: j$.util.stream.m1  reason: case insensitive filesystem */
final class CLASSNAMEm1 implements O4 {
    private final CLASSNAMEf4 a;
    final CLASSNAMEl1 b;
    final y c;

    CLASSNAMEm1(CLASSNAMEf4 f4Var, CLASSNAMEl1 l1Var, y yVar) {
        this.a = f4Var;
        this.b = l1Var;
        this.c = yVar;
    }

    public int b() {
        return CLASSNAMEe4.u | CLASSNAMEe4.r;
    }

    public Object c(CLASSNAMEz2 z2Var, j$.util.y yVar) {
        return (Boolean) new CLASSNAMEn1(this, z2Var, yVar).invoke();
    }

    public Object d(CLASSNAMEz2 z2Var, j$.util.y yVar) {
        CLASSNAMEk1 k1Var = (CLASSNAMEk1) this.c.get();
        CLASSNAMEc cVar = (CLASSNAMEc) z2Var;
        k1Var.getClass();
        cVar.n0(cVar.v0(k1Var), yVar);
        return Boolean.valueOf(k1Var.b);
    }
}
