package j$.util.stream;

import j$.util.function.o;
import j$.util.function.p;

/* renamed from: j$.util.stream.m0  reason: case insensitive filesystem */
final class CLASSNAMEm0 extends CLASSNAMEo0 implements CLASSNAMEm3 {
    final p b;

    CLASSNAMEm0(p pVar, boolean z) {
        super(z);
        this.b = pVar;
    }

    public void accept(long j) {
        this.b.accept(j);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEp1.c(this, l);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }
}
