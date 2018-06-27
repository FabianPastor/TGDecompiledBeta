package org.telegram.messenger.exoplayer2.offline;

import java.util.List;

public interface FilterableManifest<T, K> {
    T copy(List<K> list);
}
