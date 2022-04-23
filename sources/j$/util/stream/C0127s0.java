package j$.util.stream;

import j$.util.CLASSNAMEh;
import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

/* renamed from: j$.util.stream.s0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs0 implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEs0 a = new CLASSNAMEs0();

    private /* synthetic */ CLASSNAMEs0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((CLASSNAMEh) obj).b((CLASSNAMEh) obj2);
    }

    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
