package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.stream.CLASSNAMEa4;
import j$.util.w;
import j$.util.x;

class Y3 extends CLASSNAMEa4.a implements w {
    final /* synthetic */ Z3 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Y3(Z3 z3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = z3;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((p) obj2).accept(((long[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public x f(Object obj, int i, int i2) {
        return N.l((long[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public x h(int i, int i2, int i3, int i4) {
        return new Y3(this.g, i, i2, i3, i4);
    }
}
