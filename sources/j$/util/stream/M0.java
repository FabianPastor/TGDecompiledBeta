package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

public final /* synthetic */ class M0 implements BiConsumer {
    public static final /* synthetic */ M0 a = new M0();

    private /* synthetic */ M0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((CLASSNAMEi) obj).b((CLASSNAMEi) obj2);
    }

    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
