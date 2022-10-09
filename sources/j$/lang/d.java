package j$.lang;

import j$.util.function.BiConsumer;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes2.dex */
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
}
