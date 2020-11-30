package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.n;

/* renamed from: j$.util.stream.c2  reason: case insensitive filesystem */
class CLASSNAMEc2 extends CLASSNAMEw2<T, U, CLASSNAMEd2> {
    final /* synthetic */ n b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ Object d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEc2(U2 u2, n nVar, BiFunction biFunction, Object obj) {
        super(u2);
        this.b = nVar;
        this.c = biFunction;
        this.d = obj;
    }

    public CLASSNAMEu2 a() {
        return new CLASSNAMEd2(this.d, this.c, this.b);
    }
}
