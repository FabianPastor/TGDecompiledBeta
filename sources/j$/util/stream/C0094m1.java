package j$.util.stream;

import j$.util.u;

/* renamed from: j$.util.stream.m1  reason: case insensitive filesystem */
final class CLASSNAMEm1 extends CLASSNAMEd {
    private final CLASSNAMEl1 j;

    CLASSNAMEm1(CLASSNAMEl1 l1Var, CLASSNAMEy2 y2Var, u uVar) {
        super(y2Var, uVar);
        this.j = l1Var;
    }

    CLASSNAMEm1(CLASSNAMEm1 m1Var, u uVar) {
        super((CLASSNAMEd) m1Var, uVar);
        this.j = m1Var.j;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEy2 y2Var = this.a;
        CLASSNAMEj1 j1Var = (CLASSNAMEj1) this.j.c.get();
        y2Var.u0(j1Var, this.b);
        boolean z = j1Var.b;
        if (z != this.j.b.b) {
            return null;
        }
        l(Boolean.valueOf(z));
        return null;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(u uVar) {
        return new CLASSNAMEm1(this, uVar);
    }

    /* access modifiers changed from: protected */
    public Object k() {
        return Boolean.valueOf(!this.j.b.b);
    }
}
