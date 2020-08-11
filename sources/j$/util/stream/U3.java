package j$.util.stream;

import j$.util.O;
import j$.util.P;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

final class U3 extends X3 implements P {
    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void e(CLASSNAMEt tVar) {
        super.forEachRemaining(tVar);
    }

    public /* bridge */ /* synthetic */ boolean j(CLASSNAMEt tVar) {
        return super.tryAdvance(tVar);
    }

    public /* bridge */ /* synthetic */ P trySplit() {
        return (P) super.trySplit();
    }

    U3(CLASSNAMEm3 cur) {
        super(cur);
    }
}
