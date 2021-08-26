package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.j;
import j$.util.function.k;
import j$.util.v;
import j$.util.y;

final class G4 extends I4 implements v, k {
    int e;

    G4(v vVar, long j, long j2) {
        super(vVar, j, j2);
    }

    G4(v vVar, G4 g4) {
        super(vVar, g4);
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

    public k l(k kVar) {
        kVar.getClass();
        return new j(this, kVar);
    }

    /* access modifiers changed from: protected */
    public y q(y yVar) {
        return new G4((v) yVar, this);
    }

    /* access modifiers changed from: protected */
    public void s(Object obj) {
        ((k) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEk4 t(int i) {
        return new CLASSNAMEi4(i);
    }
}
