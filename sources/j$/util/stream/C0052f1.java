package j$.util.stream;

import j$.util.function.Predicate;

/* renamed from: j$.util.stream.f1  reason: case insensitive filesystem */
class CLASSNAMEf1 extends CLASSNAMEj1 {
    final /* synthetic */ CLASSNAMEk1 c;
    final /* synthetic */ Predicate d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEf1(CLASSNAMEk1 k1Var, Predicate predicate) {
        super(k1Var);
        this.c = k1Var;
        this.d = predicate;
    }

    public void accept(Object obj) {
        if (!this.a && this.d.test(obj) == this.c.a) {
            this.a = true;
            this.b = this.c.b;
        }
    }
}
