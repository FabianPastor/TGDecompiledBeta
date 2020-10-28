package j$.util.stream;

import j$.util.function.h;
import j$.util.function.y;

final class V1 extends X1 implements CLASSNAMEs5 {
    final y b;

    V1(y yVar, boolean z) {
        super(z);
        this.b = yVar;
    }

    public void accept(long j) {
        this.b.accept(j);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Long l) {
        CLASSNAMEc3.c(this, l);
    }

    public y g(y yVar) {
        yVar.getClass();
        return new h(this, yVar);
    }
}
