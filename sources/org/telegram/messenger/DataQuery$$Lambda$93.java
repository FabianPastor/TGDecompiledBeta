package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class DataQuery$$Lambda$93 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final TLObject arg$3;
    private final TL_messages_search arg$4;
    private final long arg$5;
    private final int arg$6;
    private final int arg$7;
    private final User arg$8;

    DataQuery$$Lambda$93(DataQuery dataQuery, long j, TLObject tLObject, TL_messages_search tL_messages_search, long j2, int i, int i2, User user) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = tLObject;
        this.arg$4 = tL_messages_search;
        this.arg$5 = j2;
        this.arg$6 = i;
        this.arg$7 = i2;
        this.arg$8 = user;
    }

    public void run() {
        this.arg$1.lambda$null$47$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
