package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$g5fV5AzEjpYa9k_FQikVmx9CWxY implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Message f$2;

    public /* synthetic */ -$$Lambda$DataQuery$g5fV5AzEjpYa9k_FQikVmx9CWxY(DataQuery dataQuery, long j, Message message) {
        this.f$0 = dataQuery;
        this.f$1 = j;
        this.f$2 = message;
    }

    public final void run() {
        this.f$0.lambda$putBotKeyboard$109$DataQuery(this.f$1, this.f$2);
    }
}
