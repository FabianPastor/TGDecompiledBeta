package j$.util.stream;

import j$.util.function.y;
import j$.util.function.z;

/* renamed from: j$.util.stream.d0  reason: case insensitive filesystem */
final class CLASSNAMEd0 implements O4 {
    private final CLASSNAMEf4 a;
    final boolean b;
    final Object c;
    final y d;
    final z e;

    CLASSNAMEd0(boolean z, CLASSNAMEf4 f4Var, Object obj, y yVar, z zVar) {
        this.b = z;
        this.a = f4Var;
        this.c = obj;
        this.d = yVar;
        this.e = zVar;
    }

    public int b() {
        return CLASSNAMEe4.u | (this.b ? 0 : CLASSNAMEe4.r);
    }

    public Object c(CLASSNAMEz2 z2Var, j$.util.y yVar) {
        return new CLASSNAMEj0(this, z2Var, yVar).invoke();
    }

    public Object d(CLASSNAMEz2 z2Var, j$.util.y yVar) {
        P4 p4 = (P4) this.e.get();
        CLASSNAMEc cVar = (CLASSNAMEc) z2Var;
        p4.getClass();
        cVar.n0(cVar.v0(p4), yVar);
        Object obj = p4.get();
        return obj != null ? obj : this.c;
    }
}