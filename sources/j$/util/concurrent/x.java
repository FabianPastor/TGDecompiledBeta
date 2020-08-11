package j$.util.concurrent;

import j$.util.function.BiConsumer;
import java.util.concurrent.ConcurrentMap;

public final /* synthetic */ class x {
    public static /* synthetic */ void a(ConcurrentMap concurrentMap, BiConsumer biConsumer) {
        if (concurrentMap instanceof y) {
            ((y) concurrentMap).forEach(biConsumer);
        } else {
            w.d(concurrentMap, biConsumer);
        }
    }
}
