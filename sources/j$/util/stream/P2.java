package j$.util.stream;

import j$.util.function.o;

class P2 extends U2 {
    final /* synthetic */ o b;
    final /* synthetic */ long c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    P2(CLASSNAMEe4 e4Var, o oVar, long j) {
        super(e4Var);
        this.b = oVar;
        this.c = j;
    }

    public S2 a() {
        return new Q2(this.c, this.b);
    }
}
