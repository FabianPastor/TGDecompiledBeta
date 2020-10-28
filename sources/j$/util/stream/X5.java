package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.D;
import j$.util.F;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.u;

class X5 extends CLASSNAMEb6 implements D {
    final /* synthetic */ Y5 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    X5(Y5 y5, int i, int i2, int i3, int i4) {
        super(y5, i, i2, i3, i4);
        this.g = y5;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((u) obj2).accept(((int[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.e(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public F f(Object obj, int i, int i2) {
        return V.k((int[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.b(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public F g(int i, int i2, int i3, int i4) {
        return new X5(this.g, i, i2, i3, i4);
    }
}
