package j$.util.stream;

import j$.util.function.j;

class L2 extends U2 {
    final /* synthetic */ j b;
    final /* synthetic */ int c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    L2(CLASSNAMEe4 e4Var, j jVar, int i) {
        super(e4Var);
        this.b = jVar;
        this.c = i;
    }

    public S2 a() {
        return new M2(this.c, this.b);
    }
}
