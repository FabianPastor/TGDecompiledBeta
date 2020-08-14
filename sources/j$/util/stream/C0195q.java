package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.CLASSNAMEo;

/* renamed from: j$.util.stream.q  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq implements CLASSNAMEo {
    public final /* synthetic */ BiConsumer a;

    public /* synthetic */ CLASSNAMEq(BiConsumer biConsumer) {
        this.a = biConsumer;
    }

    public final Object a(Object obj, Object obj2) {
        Object unused = this.a.accept(obj, obj2);
        return obj;
    }
}
