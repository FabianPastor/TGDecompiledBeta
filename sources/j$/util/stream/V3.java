package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import j$.util.stream.Z3;
import j$.util.u;
/* loaded from: classes2.dex */
class V3 extends Z3.a implements u.a {
    final /* synthetic */ W3 g;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public V3(W3 w3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = w3;
    }

    @Override // j$.util.stream.Z3.a
    void a(Object obj, int i, Object obj2) {
        ((j$.util.function.l) obj2).accept(((int[]) obj)[i]);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractCLASSNAMEa.k(this, consumer);
    }

    @Override // j$.util.stream.Z3.a
    j$.util.w f(Object obj, int i, int i2) {
        return j$.util.L.k((int[]) obj, i, i2 + i, 1040);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractCLASSNAMEa.c(this, consumer);
    }

    @Override // j$.util.stream.Z3.a
    j$.util.w h(int i, int i2, int i3, int i4) {
        return new V3(this.g, i, i2, i3, i4);
    }
}
