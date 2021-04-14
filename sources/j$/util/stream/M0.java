package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEa;
import java.util.LinkedHashSet;

public final /* synthetic */ class M0 implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ M0 var_a = new M0();

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
