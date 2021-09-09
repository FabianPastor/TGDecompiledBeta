package j$.util.stream;

import j$.util.function.j;

class M2 extends V2 {
    final /* synthetic */ j b;
    final /* synthetic */ int c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    M2(CLASSNAMEf4 f4Var, j jVar, int i) {
        super(f4Var);
        this.b = jVar;
        this.c = i;
    }

    public T2 a() {
        return new N2(this.c, this.b);
    }
}
