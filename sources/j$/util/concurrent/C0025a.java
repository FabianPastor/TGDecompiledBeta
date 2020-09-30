package j$.util.concurrent;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEm;
import java.util.concurrent.ConcurrentMap;

/* renamed from: j$.util.concurrent.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements BiConsumer {
    public final /* synthetic */ ConcurrentMap a;
    public final /* synthetic */ BiFunction b;

    public /* synthetic */ CLASSNAMEa(ConcurrentMap concurrentMap, BiFunction biFunction) {
        this.a = concurrentMap;
        this.b = biFunction;
    }

    public /* synthetic */ BiConsumer a(BiConsumer biConsumer) {
        return CLASSNAMEm.a(this, biConsumer);
    }

    public final void accept(Object obj, Object obj2) {
        w.h(this.a, this.b, obj, obj2);
    }
}
