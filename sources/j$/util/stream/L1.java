package j$.util.stream;

import j$.E;
import j$.util.function.CLASSNAMEf;
import j$.util.function.q;
import j$.util.function.s;
import j$.util.stream.A2;

class L1 extends M1<Double> implements A2.e {
    final /* synthetic */ N1 c;
    final /* synthetic */ s d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    L1(N1 n1, s sVar) {
        super(n1);
        this.c = n1;
        this.d = sVar;
    }

    public void accept(double d2) {
        if (!this.a && ((E) this.d).b(d2) == this.c.b) {
            this.a = true;
            this.b = this.c.c;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d2) {
        Q1.a(this, d2);
    }

    public q j(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }
}
