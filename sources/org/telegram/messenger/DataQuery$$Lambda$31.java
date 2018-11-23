package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class DataQuery$$Lambda$31 implements RequestDelegate {
    private final DataQuery arg$1;
    private final long arg$2;
    private final TL_messages_search arg$3;
    private final long arg$4;
    private final int arg$5;
    private final int arg$6;
    private final User arg$7;

    DataQuery$$Lambda$31(DataQuery dataQuery, long j, TL_messages_search tL_messages_search, long j2, int i, int i2, User user) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = tL_messages_search;
        this.arg$4 = j2;
        this.arg$5 = i;
        this.arg$6 = i2;
        this.arg$7 = user;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchMessagesInChat$48$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
