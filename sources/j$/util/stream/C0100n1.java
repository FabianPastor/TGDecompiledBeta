package j$.util.stream;

import j$.util.y;

/* renamed from: j$.util.stream.n1  reason: case insensitive filesystem */
final class CLASSNAMEn1 extends CLASSNAMEd {
    private final CLASSNAMEm1 j;

    CLASSNAMEn1(CLASSNAMEm1 m1Var, CLASSNAMEz2 z2Var, y yVar) {
        super(z2Var, yVar);
        this.j = m1Var;
    }

    CLASSNAMEn1(CLASSNAMEn1 n1Var, y yVar) {
        super((CLASSNAMEd) n1Var, yVar);
        this.j = n1Var.j;
    }

    /* access modifiers changed from: protected */
    public Object a() {
        CLASSNAMEz2 z2Var = this.a;
        CLASSNAMEk1 k1Var = (CLASSNAMEk1) this.j.c.get();
        z2Var.u0(k1Var, this.b);
        boolean z = k1Var.b;
        if (z != this.j.b.b) {
            return null;
        }
        l(Boolean.valueOf(z));
        return null;
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEf f(y yVar) {
        return new CLASSNAMEn1(this, yVar);
    }

    /* access modifiers changed from: protected */
    public Object k() {
        return Boolean.valueOf(!this.j.b.b);
    }
}
