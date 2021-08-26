package j$.util.stream;

import j$.util.CLASSNAMEg;
import j$.util.concurrent.a;
import j$.util.function.BiConsumer;

/* renamed from: j$.util.stream.t  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEt implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEt a = new CLASSNAMEt();

    private /* synthetic */ CLASSNAMEt() {
    }

    public final void accept(Object obj, Object obj2) {
        ((CLASSNAMEg) obj).b((CLASSNAMEg) obj2);
    }

    public BiConsumer c(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
