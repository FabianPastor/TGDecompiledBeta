package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.BotInfo;

final /* synthetic */ class DataQuery$$Lambda$70 implements Runnable {
    private final DataQuery arg$1;
    private final BotInfo arg$2;

    DataQuery$$Lambda$70(DataQuery dataQuery, BotInfo botInfo) {
        this.arg$1 = dataQuery;
        this.arg$2 = botInfo;
    }

    public void run() {
        this.arg$1.lambda$putBotInfo$110$DataQuery(this.arg$2);
    }
}
