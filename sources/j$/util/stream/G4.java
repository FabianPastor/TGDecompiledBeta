package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.function.q;
import j$.util.u;
import j$.util.v;

final class G4 extends H4 implements v, q {
    long e;

    G4(v vVar, long j, long j2) {
        super(vVar, j, j2);
    }

    G4(v vVar, G4 g4) {
        super(vVar, g4);
    }

    public void accept(long j) {
        this.e = j;
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    public q f(q qVar) {
        qVar.getClass();
        return new p(this, qVar);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }

    /* access modifiers changed from: protected */
    public u q(u uVar) {
        return new G4((v) uVar, this);
    }

    /* access modifiers changed from: protected */
    public void s(Object obj) {
        ((q) obj).accept(this.e);
    }

    /* access modifiers changed from: protected */
    public CLASSNAMEj4 t(int i) {
        return new CLASSNAMEi4(i);
    }
}
