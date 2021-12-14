package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.stream.CLASSNAMEa4;
import j$.util.v;
import j$.util.x;

class W3 extends CLASSNAMEa4.a implements v {
    final /* synthetic */ X3 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    W3(X3 x3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = x3;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((l) obj2).accept(((int[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.k(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public x f(Object obj, int i, int i2) {
        return N.k((int[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.c(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public x h(int i, int i2, int i3, int i4) {
        return new W3(this.g, i, i2, i3, i4);
    }
}
