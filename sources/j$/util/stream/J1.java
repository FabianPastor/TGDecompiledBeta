package j$.util.stream;

import j$.V;
import j$.util.function.CLASSNAMEg;
import j$.util.function.w;
import j$.util.function.y;
import j$.util.stream.A2;

class J1 extends M1<Integer> implements A2.f {
    final /* synthetic */ N1 c;
    final /* synthetic */ y d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    J1(N1 n1, y yVar) {
        super(n1);
        this.c = n1;
        this.d = yVar;
    }

    public void accept(int i) {
        if (!this.a && ((V) this.d).b(i) == this.c.b) {
            this.a = true;
            this.b = this.c.c;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        Q1.b(this, num);
    }

    public w k(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }
}
