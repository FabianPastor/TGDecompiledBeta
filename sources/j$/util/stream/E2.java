package j$.util.stream;

import j$.util.function.CLASSNAMEb;
import j$.util.function.d;
import j$.util.function.i;
import j$.util.function.n;

class E2 extends V2 {
    public final /* synthetic */ int b = 1;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, CLASSNAMEb bVar) {
        super(f4Var);
        this.c = bVar;
    }

    public T2 a() {
        switch (this.b) {
            case 0:
                return new F2((d) this.c);
            case 1:
                return new I2((CLASSNAMEb) this.c);
            case 2:
                return new O2((i) this.c);
            default:
                return new S2((n) this.c);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, d dVar) {
        super(f4Var);
        this.c = dVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, i iVar) {
        super(f4Var);
        this.c = iVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, n nVar) {
        super(f4Var);
        this.c = nVar;
    }
}
