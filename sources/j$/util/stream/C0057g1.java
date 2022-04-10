package j$.util.stream;

import j$.util.function.Predicate;

/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
class CLASSNAMEg1 extends CLASSNAMEk1 {
    final /* synthetic */ CLASSNAMEl1 c;
    final /* synthetic */ Predicate d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEg1(CLASSNAMEl1 l1Var, Predicate predicate) {
        super(l1Var);
        this.c = l1Var;
        this.d = predicate;
    }

    public void accept(Object obj) {
        if (!this.a && this.d.test(obj) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
