package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.BotInfo;

final /* synthetic */ class DataQuery$$Lambda$83 implements Runnable {
    private final DataQuery arg$1;
    private final BotInfo arg$2;
    private final int arg$3;

    DataQuery$$Lambda$83(DataQuery dataQuery, BotInfo botInfo, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = botInfo;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$107$DataQuery(this.arg$2, this.arg$3);
    }
}
