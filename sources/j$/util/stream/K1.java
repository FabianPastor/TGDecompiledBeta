package j$.util.stream;

import j$.CLASSNAMEj0;
import j$.util.function.C;
import j$.util.function.CLASSNAMEh;
import j$.util.function.E;
import j$.util.stream.A2;

class K1 extends M1<Long> implements A2.g {
    final /* synthetic */ N1 c;
    final /* synthetic */ E d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    K1(N1 n1, E e) {
        super(n1);
        this.c = n1;
        this.d = e;
    }

    public void accept(long j) {
        if (!this.a && ((CLASSNAMEj0) this.d).b(j) == this.c.b) {
            this.a = true;
            this.b = this.c.c;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        Q1.c(this, l);
    }

    public C f(C c2) {
        c2.getClass();
        return new CLASSNAMEh(this, c2);
    }
}
