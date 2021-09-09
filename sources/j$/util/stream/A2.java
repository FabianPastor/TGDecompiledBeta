package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEb;
import j$.util.function.u;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.function.z;

class A2 extends V2 {
    public final /* synthetic */ int b = 3;
    final /* synthetic */ Object c;
    final /* synthetic */ Object d;
    final /* synthetic */ Object e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public A2(CLASSNAMEf4 f4Var, BiConsumer biConsumer, BiConsumer biConsumer2, z zVar) {
        super(f4Var);
        this.c = biConsumer;
        this.d = biConsumer2;
        this.e = zVar;
    }

    public T2 a() {
        switch (this.b) {
            case 0:
                return new B2((z) this.e, (w) this.d, (CLASSNAMEb) this.c);
            case 1:
                return new G2((z) this.e, (u) this.d, (CLASSNAMEb) this.c);
            case 2:
                return new H2(this.e, (BiFunction) this.d, (CLASSNAMEb) this.c);
            case 3:
                return new L2((z) this.e, (BiConsumer) this.d, (BiConsumer) this.c);
            default:
                return new P2((z) this.e, (v) this.d, (CLASSNAMEb) this.c);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public A2(CLASSNAMEf4 f4Var, CLASSNAMEb bVar, BiFunction biFunction, Object obj) {
        super(f4Var);
        this.c = bVar;
        this.d = biFunction;
        this.e = obj;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public A2(CLASSNAMEf4 f4Var, CLASSNAMEb bVar, u uVar, z zVar) {
        super(f4Var);
        this.c = bVar;
        this.d = uVar;
        this.e = zVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public A2(CLASSNAMEf4 f4Var, CLASSNAMEb bVar, v vVar, z zVar) {
        super(f4Var);
        this.c = bVar;
        this.d = vVar;
        this.e = zVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public A2(CLASSNAMEf4 f4Var, CLASSNAMEb bVar, w wVar, z zVar) {
        super(f4Var);
        this.c = bVar;
        this.d = wVar;
        this.e = zVar;
    }
}
