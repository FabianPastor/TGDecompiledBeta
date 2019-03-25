package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_topPeer;

final /* synthetic */ class DataQuery$$Lambda$92 implements Comparator {
    static final Comparator $instance = new DataQuery$$Lambda$92();

    private DataQuery$$Lambda$92() {
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$null$79$DataQuery((TL_topPeer) obj, (TL_topPeer) obj2);
    }
}
