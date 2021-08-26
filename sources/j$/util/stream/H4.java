package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.o;
import j$.util.function.p;
import j$.util.w;
import j$.util.y;

final class H4 extends I4 implements w, p {
    long e;

    H4(w wVar, long j, long j2) {
        super(wVar, j, j2);
    }

    H4(w wVar, H4 h4) {
        super(wVar, h4);
    }

    public void accept(long j) {
        this.e = j;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    public p f(p pVar) {
        pVar.getClass();
        return new o(this, pVar);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }

    /* access modifiers changed from: protected */
    public y q(y yVar) {
        return new H4((w) yVar, this);
    }

    /* access modifiers changed from: protected */
    public void s(Object obj) {
        ((p) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk4 t(int i) {
        return new CLASSNAMEj4(i);
    }
}
