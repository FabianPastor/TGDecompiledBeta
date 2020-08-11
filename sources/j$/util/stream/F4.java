package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.V;

class F4 extends T4 {
    final /* synthetic */ BiConsumer b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ V d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    F4(CLASSNAMEv6 shape, BiConsumer biConsumer, BiConsumer biConsumer2, V v) {
        super(shape);
        this.b = biConsumer;
        this.c = biConsumer2;
        this.d = v;
    }

    /* renamed from: e */
    public G4 b() {
        return new G4(this.d, this.c, this.b);
    }
}
