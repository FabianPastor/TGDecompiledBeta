package j$.util.stream;

import j$.util.function.Predicate;

class X2 extends CLASSNAMEb3 {
    final /* synthetic */ CLASSNAMEc3 c;
    final /* synthetic */ Predicate d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    X2(CLASSNAMEc3 c3Var, Predicate predicate) {
        super(c3Var);
        this.c = c3Var;
        this.d = predicate;
    }

    public void accept(Object t) {
        if (!this.a && this.d.test(t) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
