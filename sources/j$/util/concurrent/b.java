package j$.util.concurrent;

import j$.util.Map;
import j$.util.function.BiConsumer;

public interface b<K, V> extends Map<K, V> {
    void forEach(BiConsumer biConsumer);
}
