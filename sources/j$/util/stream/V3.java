package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.stream.Z3;
import j$.util.u;
import j$.util.w;

class V3 extends Z3.a implements u.a {
    final /* synthetic */ W3 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    V3(W3 w3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = w3;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((l) obj2).accept(((int[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public w f(Object obj, int i, int i2) {
        return L.k((int[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public w h(int i, int i2, int i3, int i4) {
        return new V3(this.g, i, i2, i3, i4);
    }
}
