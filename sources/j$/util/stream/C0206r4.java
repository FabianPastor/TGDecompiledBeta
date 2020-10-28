package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.n;

/* renamed from: j$.util.stream.r4  reason: case insensitive filesystem */
class CLASSNAMEr4 extends L4 {
    final /* synthetic */ n b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ Object d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEr4(CLASSNAMEh6 h6Var, n nVar, BiFunction biFunction, Object obj) {
        super(h6Var);
        this.b = nVar;
        this.c = biFunction;
        this.d = obj;
    }

    public J4 a() {
        return new CLASSNAMEs4(this.d, this.c, this.b);
    }
}
