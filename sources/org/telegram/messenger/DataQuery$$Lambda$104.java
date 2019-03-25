package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class DataQuery$$Lambda$104 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final TLObject arg$3;
    private final TL_messages_search arg$4;
    private final long arg$5;
    private final long arg$6;
    private final int arg$7;
    private final long arg$8;
    private final User arg$9;

    DataQuery$$Lambda$104(DataQuery dataQuery, int i, TLObject tLObject, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = tLObject;
        this.arg$4 = tL_messages_search;
        this.arg$5 = j;
        this.arg$6 = j2;
        this.arg$7 = i2;
        this.arg$8 = j3;
        this.arg$9 = user;
    }

    public void run() {
        this.arg$1.lambda$null$49$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
