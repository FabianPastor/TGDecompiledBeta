package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.b;
import j$.util.function.y;
import j$.wrappers.J0;

class I2 extends U2 {
    final /* synthetic */ b b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ y d;
    final /* synthetic */ J0 e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    I2(CLASSNAMEe4 e4Var, b bVar, BiConsumer biConsumer, y yVar, J0 j0) {
        super(e4Var);
        this.b = bVar;
        this.c = biConsumer;
        this.d = yVar;
        this.e = j0;
    }

    public S2 a() {
        return new J2(this.d, this.c, this.b);
    }

    public int b() {
        if (this.e.b().contains(CLASSNAMEh.UNORDERED)) {
            return CLASSNAMEd4.r;
        }
        return 0;
    }
}
