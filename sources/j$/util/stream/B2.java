package j$.util.stream;

import j$.util.function.d;

class B2 extends U2 {
    final /* synthetic */ d b;
    final /* synthetic */ double c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    B2(CLASSNAMEe4 e4Var, d dVar, double d) {
        super(e4Var);
        this.b = dVar;
        this.c = d;
    }

    public S2 a() {
        return new C2(this.c, this.b);
    }
}
