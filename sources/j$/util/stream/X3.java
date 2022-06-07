package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.stream.Z3;
import j$.util.v;
import j$.util.w;

class X3 extends Z3.a implements v {
    final /* synthetic */ Y3 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    X3(Y3 y3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = y3;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((q) obj2).accept(((long[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.l(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public w f(Object obj, int i, int i2) {
        return L.l((long[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.d(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public w h(int i, int i2, int i3, int i4) {
        return new X3(this.g, i, i2, i3, i4);
    }
}
