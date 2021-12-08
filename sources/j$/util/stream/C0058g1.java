package j$.util.stream;

import j$.util.function.y;

/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
class CLASSNAMEg1 extends CLASSNAMEk1 {
    final /* synthetic */ CLASSNAMEl1 c;
    final /* synthetic */ y d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEg1(CLASSNAMEl1 l1Var, y yVar) {
        super(l1Var);
        this.c = l1Var;
        this.d = yVar;
    }

    public void accept(Object obj) {
        if (!this.a && this.d.test(obj) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
