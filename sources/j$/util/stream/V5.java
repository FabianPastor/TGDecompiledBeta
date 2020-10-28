package j$.util.stream;

import j$.util.C;
import j$.util.CLASSNAMEw;
import j$.util.F;
import j$.util.V;
import j$.util.function.Consumer;
import j$.util.function.q;

class V5 extends CLASSNAMEb6 implements C {
    final /* synthetic */ W5 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    V5(W5 w5, int i, int i2, int i3, int i4) {
        super(w5, i, i2, i3, i4);
        this.g = w5;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((q) obj2).accept(((double[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEw.d(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public F f(Object obj, int i, int i2) {
        return V.j((double[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEw.a(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public F g(int i, int i2, int i3, int i4) {
        return new V5(this.g, i, i2, i3, i4);
    }
}
