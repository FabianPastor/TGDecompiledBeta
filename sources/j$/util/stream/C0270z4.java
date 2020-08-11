package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEo;

/* renamed from: j$.util.stream.z4  reason: case insensitive filesystem */
class CLASSNAMEz4 extends T4 {
    final /* synthetic */ CLASSNAMEo b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ Object d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEz4(CLASSNAMEv6 shape, CLASSNAMEo oVar, BiFunction biFunction, Object obj) {
        super(shape);
        this.b = oVar;
        this.c = biFunction;
        this.d = obj;
    }

    /* renamed from: e */
    public A4 b() {
        return new A4(this.d, this.c, this.b);
    }
}
