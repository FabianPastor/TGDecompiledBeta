package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.messages_Messages;

final /* synthetic */ class DataQuery$$Lambda$36 implements Runnable {
    private final DataQuery arg$1;
    private final messages_Messages arg$2;
    private final boolean arg$3;
    private final long arg$4;
    private final ArrayList arg$5;
    private final int arg$6;
    private final int arg$7;
    private final boolean arg$8;

    DataQuery$$Lambda$36(DataQuery dataQuery, messages_Messages messages_messages, boolean z, long j, ArrayList arrayList, int i, int i2, boolean z2) {
        this.arg$1 = dataQuery;
        this.arg$2 = messages_messages;
        this.arg$3 = z;
        this.arg$4 = j;
        this.arg$5 = arrayList;
        this.arg$6 = i;
        this.arg$7 = i2;
        this.arg$8 = z2;
    }

    public void run() {
        this.arg$1.lambda$processLoadedMedia$59$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
