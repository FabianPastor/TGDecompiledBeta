package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEo;

public final /* synthetic */ class L implements CLASSNAMEo {
    public final /* synthetic */ BiConsumer a;

    public /* synthetic */ L(BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public final Object a(Object obj, Object obj2) {
        Object unused = this.a.accept(obj, obj2);
        return obj;
    }
}
