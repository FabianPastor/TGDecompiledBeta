package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEm;
import java.util.LinkedHashSet;

/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg1 implements BiConsumer {
    public static final /* synthetic */ CLASSNAMEg1 a = new CLASSNAMEg1();

    private /* synthetic */ CLASSNAMEg1() {
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return CLASSNAMEm.a(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).add(obj2);
    }
}
