package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.E;

/* renamed from: j$.util.stream.x4  reason: case insensitive filesystem */
class CLASSNAMEx4 extends L4 {
    final /* synthetic */ BiConsumer b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ E d;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CLASSNAMEx4(CLASSNAMEh6 h6Var, BiConsumer biConsumer, BiConsumer biConsumer2, E e) {
        super(h6Var);
        this.b = biConsumer;
        this.c = biConsumer2;
        this.d = e;
    }

    public J4 a() {
        return new CLASSNAMEy4(this.d, this.c, this.b);
    }
}
