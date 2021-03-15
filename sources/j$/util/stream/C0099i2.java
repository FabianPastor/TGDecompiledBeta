package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.J;

/* renamed from: j$.util.stream.i2  reason: case insensitive filesystem */
class CLASSNAMEi2 extends CLASSNAMEw2<T, R, CLASSNAMEj2> {
    final /* synthetic */ BiConsumer b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ J d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEi2(U2 u2, BiConsumer biConsumer, BiConsumer biConsumer2, J j) {
        super(u2);
        this.b = biConsumer;
        this.c = biConsumer2;
        this.d = j;
    }

    public CLASSNAMEu2 a() {
        return new CLASSNAMEj2(this.d, this.c, this.b);
    }
}
