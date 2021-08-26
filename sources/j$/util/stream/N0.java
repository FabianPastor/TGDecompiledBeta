package j$.util.stream;

import j$.util.CLASSNAMEi;
import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

public final /* synthetic */ class N0 implements BiConsumer {
    public static final /* synthetic */ N0 a = new N0();

    private /* synthetic */ N0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((CLASSNAMEi) obj).b((CLASSNAMEi) obj2);
    }

    public BiConsumer c(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
