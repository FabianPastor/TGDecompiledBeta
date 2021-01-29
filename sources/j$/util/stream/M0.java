package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEa;
import java.util.LinkedHashSet;

public final /* synthetic */ class M0 implements BiConsumer {
    public static final /* synthetic */ M0 a = new M0();

    private /* synthetic */ M0() {
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).add(obj2);
    }
}
