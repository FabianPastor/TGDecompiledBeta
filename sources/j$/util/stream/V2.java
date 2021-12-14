package j$.util.stream;

import j$.util.y;

abstract class V2 implements O4 {
    private final CLASSNAMEf4 a;

    V2(CLASSNAMEf4 f4Var) {
        this.a = f4Var;
    }

    public abstract T2 a();

    public /* synthetic */ int b() {
        return 0;
    }

    public Object c(CLASSNAMEz2 z2Var, y yVar) {
        return ((T2) new W2(this, z2Var, yVar).invoke()).get();
    }

    public Object d(CLASSNAMEz2 z2Var, y yVar) {
        T2 a2 = a();
        CLASSNAMEc cVar = (CLASSNAMEc) z2Var;
        a2.getClass();
        cVar.n0(cVar.v0(a2), yVar);
        return a2.get();
    }
}
