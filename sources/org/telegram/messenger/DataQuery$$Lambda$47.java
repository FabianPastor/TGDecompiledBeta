package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_topPeer;

final /* synthetic */ class DataQuery$$Lambda$47 implements Comparator {
    static final Comparator $instance = new DataQuery$$Lambda$47();

    private DataQuery$$Lambda$47() {
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$increaseInlineRaiting$76$DataQuery((TL_topPeer) obj, (TL_topPeer) obj2);
    }
}
