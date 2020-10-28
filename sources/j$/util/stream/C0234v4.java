package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.E;
import j$.util.function.n;

/* renamed from: j$.util.stream.v4  reason: case insensitive filesystem */
class CLASSNAMEv4 extends L4 {
    final /* synthetic */ n b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ E d;
    final /* synthetic */ CLASSNAMEn1 e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEv4(CLASSNAMEh6 h6Var, n nVar, BiConsumer biConsumer, E e2, CLASSNAMEn1 n1Var) {
        super(h6Var);
        this.b = nVar;
        this.c = biConsumer;
        this.d = e2;
        this.e = n1Var;
    }

    public J4 a() {
        return new CLASSNAMEw4(this.d, this.c, this.b);
    }

    public int b() {
        if (this.e.characteristics().contains(CLASSNAMEm1.UNORDERED)) {
            return CLASSNAMEg6.w;
        }
        return 0;
    }
}
