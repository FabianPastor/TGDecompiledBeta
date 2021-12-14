package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiConsumer;
import java.util.LinkedHashSet;

/* renamed from: j$.util.stream.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEn a = new CLASSNAMEn();

    private /* synthetic */ CLASSNAMEn() {
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).addAll((LinkedHashSet) obj2);
    }

    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
