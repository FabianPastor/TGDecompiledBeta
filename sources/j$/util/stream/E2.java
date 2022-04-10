package j$.util.stream;

import j$.util.function.b;
import j$.util.function.d;
import j$.util.function.j;
import j$.util.function.o;

class E2 extends V2 {
    public final /* synthetic */ int b = 1;
    final /* synthetic */ Object c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, b bVar) {
        super(f4Var);
        this.c = bVar;
    }

    public T2 a() {
        switch (this.b) {
            case 0:
                return new F2((d) this.c);
            case 1:
                return new I2((b) this.c);
            case 2:
                return new O2((j) this.c);
            default:
                return new S2((o) this.c);
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, d dVar) {
        super(f4Var);
        this.c = dVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, j jVar) {
        super(f4Var);
        this.c = jVar;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public E2(CLASSNAMEf4 f4Var, o oVar) {
        super(f4Var);
        this.c = oVar;
    }
}
