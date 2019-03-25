package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;

final /* synthetic */ class DataQuery$$Lambda$94 implements Runnable {
    private final DataQuery arg$1;
    private final TL_contacts_topPeers arg$2;

    DataQuery$$Lambda$94(DataQuery dataQuery, TL_contacts_topPeers tL_contacts_topPeers) {
        this.arg$1 = dataQuery;
        this.arg$2 = tL_contacts_topPeers;
    }

    public void run() {
        this.arg$1.lambda$null$71$DataQuery(this.arg$2);
    }
}
