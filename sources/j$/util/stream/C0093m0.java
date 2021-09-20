package j$.util.stream;

import j$.util.function.p;
import j$.util.function.q;

/* renamed from: j$.util.stream.m0  reason: case insensitive filesystem */
final class CLASSNAMEm0 extends CLASSNAMEo0 implements CLASSNAMEm3 {
    final q b;

    CLASSNAMEm0(q qVar, boolean z) {
        super(z);
        this.b = qVar;
    }

    public void accept(long j) {
        this.b.accept(j);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }
}
