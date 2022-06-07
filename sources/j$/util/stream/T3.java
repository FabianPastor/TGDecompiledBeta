package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.L;
import j$.util.function.Consumer;
import j$.util.function.f;
import j$.util.stream.Z3;
import j$.util.t;
import j$.util.w;

class T3 extends Z3.a implements t {
    final /* synthetic */ U3 g;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    T3(U3 u3, int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.g = u3;
    }

    /* access modifiers changed from: package-private */
    public void a(Object obj, int i, Object obj2) {
        ((f) obj2).accept(((double[]) obj)[i]);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return CLASSNAMEa.j(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public w f(Object obj, int i, int i2) {
        return L.j((double[]) obj, i, i2 + i, 1040);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        CLASSNAMEa.b(this, consumer);
    }

    /* access modifiers changed from: package-private */
    public w h(int i, int i2, int i3, int i4) {
        return new T3(this.g, i, i2, i3, i4);
    }
}
