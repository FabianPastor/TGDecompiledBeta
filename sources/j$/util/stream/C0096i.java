package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEa;
import java.util.LinkedHashSet;

/* renamed from: j$.util.stream.i  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi implements BiConsumer {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ CLASSNAMEi var_a = new CLASSNAMEi();

    private /* synthetic */ CLASSNAMEi() {
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).addAll((LinkedHashSet) obj2);
    }
}
