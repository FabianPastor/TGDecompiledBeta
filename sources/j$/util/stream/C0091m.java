package j$.util.stream;

import j$.util.concurrent.a;
import j$.util.function.BiConsumer;
import java.util.LinkedHashSet;

/* renamed from: j$.util.stream.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEm a = new CLASSNAMEm();

    private /* synthetic */ CLASSNAMEm() {
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).add(obj2);
    }

    public BiConsumer c(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new a((BiConsumer) this, biConsumer);
    }
}
