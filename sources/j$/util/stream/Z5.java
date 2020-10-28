package j$.util.stream;

import j$.util.CLASSNAMEw;
import j$.util.E;
import j$.util.F;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.y;

class Z5 extends CLASSNAMEb6 implements E {
    final /* synthetic */ CLASSNAMEa6 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Z5(CLASSNAMEa6 a6Var, int i, int i2, int i3, int i4) {
        super(a6Var, i, i2, i3, i4);
        this.g = a6Var;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((y) obj2).accept(((long[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.f(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public F f(Object obj, int i, int i2) {
        return V.l((long[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.c(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public F g(int i, int i2, int i3, int i4) {
        return new Z5(this.g, i, i2, i3, i4);
    }
}
