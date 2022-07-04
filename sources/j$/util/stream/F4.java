package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.k;
import j$.util.function.l;
import j$.util.u;

final class F4 extends H4 implements u.a, l {
    int e;

    F4(u.a aVar, long j, long j2) {
        super(aVar, j, j2);
    }

    F4(u.a aVar, F4 f4) {
        super(aVar, f4);
    }

    public void accept(int i) {
        this.e = i;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }

    public l l(l lVar) {
        lVar.getClass();
        return new k(this, lVar);
    }

    /* access modifiers changed from: protected */
    public u q(u uVar) {
        return new F4((u.a) uVar, this);
    }

    /* access modifiers changed from: protected */
    public void s(Object obj) {
        ((l) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEj4 t(int i) {
        return new CLASSNAMEh4(i);
    }
}
