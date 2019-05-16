package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$d7oypRr3g0xB92hPNQy49uD_p8A implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Message f$2;

    public /* synthetic */ -$$Lambda$DataQuery$d7oypRr3g0xB92hPNQy49uD_p8A(DataQuery dataQuery, long j, Message message) {
        this.f$0 = dataQuery;
        this.f$1 = j;
        this.f$2 = message;
    }

    public final void run() {
        this.f$0.lambda$saveDraftReplyMessage$103$DataQuery(this.f$1, this.f$2);
    }
}
