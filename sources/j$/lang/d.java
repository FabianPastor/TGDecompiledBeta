package j$.lang;

import j$.util.function.BiConsumer;
import j$.util.function.y;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public abstract /* synthetic */ class d {
    public static void a(ConcurrentMap concurrentMap, BiConsumer biConsumer) {
        biConsumer.getClass();
        for (Map.Entry entry : concurrentMap.entrySet()) {
            try {
                biConsumer.accept(entry.getKey(), entry.getValue());
            } catch (IllegalStateException unused) {
            }
        }
    }

    public static /* synthetic */ boolean b(y yVar, y yVar2, Object obj) {
        return yVar.test(obj) && yVar2.test(obj);
    }
}
