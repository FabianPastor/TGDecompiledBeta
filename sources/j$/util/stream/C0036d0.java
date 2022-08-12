package j$.util.stream;

import j$.util.function.Predicate;
import j$.util.function.y;
import j$.util.u;

/* renamed from: j$.util.stream.d0  reason: case insensitive filesystem */
final class CLASSNAMEd0 implements N4 {
    private final CLASSNAMEe4 a;
    final boolean b;
    final Object c;
    final Predicate d;
    final y e;

    CLASSNAMEd0(boolean z, CLASSNAMEe4 e4Var, Object obj, Predicate predicate, y yVar) {
        this.b = z;
        this.a = e4Var;
        this.c = obj;
        this.d = predicate;
        this.e = yVar;
    }

    public int b() {
        return CLASSNAMEd4.u | (this.b ? 0 : CLASSNAMEd4.r);
    }

    public Object c(CLASSNAMEy2 y2Var, u uVar) {
        return new CLASSNAMEj0(this, y2Var, uVar).invoke();
    }

    public Object d(CLASSNAMEy2 y2Var, u uVar) {
        O4 o4 = (O4) this.e.get();
        CLASSNAMEc cVar = (CLASSNAMEc) y2Var;
        o4.getClass();
        cVar.n0(cVar.v0(o4), uVar);
        Object obj = o4.get();
        return obj != null ? obj : this.c;
    }
}
