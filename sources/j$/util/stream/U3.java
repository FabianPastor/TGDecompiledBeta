package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.N;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.stream.CLASSNAMEa4;
import j$.util.u;
import j$.util.x;

class U3 extends CLASSNAMEa4.a implements u {
    final /* synthetic */ V3 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    U3(V3 v3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = v3;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((f) obj2).accept(((double[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public x f(Object obj, int i, int i2) {
        return N.j((double[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public x h(int i, int i2, int i3, int i4) {
        return new U3(this.g, i, i2, i3, i4);
    }
}
