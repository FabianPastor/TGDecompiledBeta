package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEm;
import java.util.LinkedHashSet;

public final /* synthetic */ class X0 implements BiConsumer {
    public static final /* synthetic */ X0 a = new X0();

    private /* synthetic */ X0() {
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return CLASSNAMEm.a(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).addAll((LinkedHashSet) obj2);
    }
}
