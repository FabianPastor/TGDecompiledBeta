package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.b;
import j$.util.function.y;
import j$.wrappers.J0;

class J2 extends V2 {
    final /* synthetic */ b b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ y d;
    final /* synthetic */ J0 e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    J2(CLASSNAMEf4 f4Var, b bVar, BiConsumer biConsumer, y yVar, J0 j0) {
        super(f4Var);
        this.b = bVar;
        this.c = biConsumer;
        this.d = yVar;
        this.e = j0;
    }

    public T2 a() {
        return new K2(this.d, this.c, this.b);
    }

    public int b() {
        if (this.e.b().contains(CLASSNAMEh.UNORDERED)) {
            return CLASSNAMEe4.r;
        }
        return 0;
    }
}
