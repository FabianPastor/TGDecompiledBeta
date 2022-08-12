package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.b;
import j$.util.function.u;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.function.y;

/* renamed from: j$.util.stream.z2  reason: case insensitive filesystem */
class CLASSNAMEz2 extends U2 {
    public final /* synthetic */ int b = 3;
    final /* synthetic */ Object c;
    final /* synthetic */ Object d;
    final /* synthetic */ Object e;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(CLASSNAMEe4 e4Var, BiConsumer biConsumer, BiConsumer biConsumer2, y yVar) {
        super(e4Var);
        this.c = biConsumer;
        this.d = biConsumer2;
        this.e = yVar;
    }

    public S2 a() {
        switch (this.b) {
            case 0:
                return new A2((y) this.e, (w) this.d, (b) this.c);
            case 1:
                return new F2((y) this.e, (u) this.d, (b) this.c);
            case 2:
                return new G2(this.e, (BiFunction) this.d, (b) this.c);
            case 3:
                return new K2((y) this.e, (BiConsumer) this.d, (BiConsumer) this.c);
            default:
                return new O2((y) this.e, (v) this.d, (b) this.c);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(CLASSNAMEe4 e4Var, b bVar, BiFunction biFunction, Object obj) {
        super(e4Var);
        this.c = bVar;
        this.d = biFunction;
        this.e = obj;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(CLASSNAMEe4 e4Var, b bVar, u uVar, y yVar) {
        super(e4Var);
        this.c = bVar;
        this.d = uVar;
        this.e = yVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(CLASSNAMEe4 e4Var, b bVar, v vVar, y yVar) {
        super(e4Var);
        this.c = bVar;
        this.d = vVar;
        this.e = yVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CLASSNAMEz2(CLASSNAMEe4 e4Var, b bVar, w wVar, y yVar) {
        super(e4Var);
        this.c = bVar;
        this.d = wVar;
        this.e = yVar;
    }
}
