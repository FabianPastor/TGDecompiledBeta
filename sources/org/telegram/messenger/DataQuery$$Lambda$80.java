package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_topPeer;

final /* synthetic */ class DataQuery$$Lambda$80 implements Comparator {
    static final Comparator $instance = new DataQuery$$Lambda$80();

    private DataQuery$$Lambda$80() {
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$null$79$DataQuery((TL_topPeer) obj, (TL_topPeer) obj2);
    }
}
