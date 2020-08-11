package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEo;
import j$.util.function.V;

class D4 extends T4 {
    final /* synthetic */ CLASSNAMEo b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ V d;
    final /* synthetic */ CLASSNAMEn1 e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    D4(CLASSNAMEv6 shape, CLASSNAMEo oVar, BiConsumer biConsumer, V v, CLASSNAMEn1 n1Var) {
        super(shape);
        this.b = oVar;
        this.c = biConsumer;
        this.d = v;
        this.e = n1Var;
    }

    /* renamed from: e */
    public E4 b() {
        return new E4(this.d, this.c, this.b);
    }

    public int a() {
        if (this.e.characteristics().contains(CLASSNAMEm1.UNORDERED)) {
            return CLASSNAMEu6.w;
        }
        return 0;
    }
}
