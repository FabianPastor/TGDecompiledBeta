package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.J;
import j$.util.function.n;
import j$.util.stream.CLASSNAMEm1;

/* renamed from: j$.util.stream.g2  reason: case insensitive filesystem */
class CLASSNAMEg2 extends CLASSNAMEw2<T, I, CLASSNAMEh2> {
    final /* synthetic */ n b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ J d;
    final /* synthetic */ CLASSNAMEm1 e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEg2(U2 u2, n nVar, BiConsumer biConsumer, J j, CLASSNAMEm1 m1Var) {
        super(u2);
        this.b = nVar;
        this.c = biConsumer;
        this.d = j;
        this.e = m1Var;
    }

    public CLASSNAMEu2 a() {
        return new CLASSNAMEh2(this.d, this.c, this.b);
    }

    public int b() {
        if (this.e.characteristics().contains(CLASSNAMEm1.a.UNORDERED)) {
            return T2.r;
        }
        return 0;
    }
}
